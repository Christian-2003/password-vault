package de.christian2003.feature.export.infrastructure.backup.v2

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.core.common.domain.services.FilesystemService
import de.christian2003.data.accounts.application.usecases.CreateAccountsBatchUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.BatchOption
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.feature.export.R
import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.entities.ImportDescriptor
import de.christian2003.feature.export.domain.entities.ImportOption
import de.christian2003.feature.export.domain.entities.ImportProperties
import de.christian2003.feature.export.domain.exceptions.CorruptImportFileException
import de.christian2003.feature.export.domain.exceptions.IllegalImportConfigException
import de.christian2003.feature.export.domain.exceptions.InvalidKeyException
import de.christian2003.feature.export.domain.exceptions.UnsupportedFileFormatException
import de.christian2003.feature.export.domain.services.ImportService
import de.christian2003.feature.export.infrastructure.backup.v2.dto.V2BackupDetailDto
import de.christian2003.feature.export.infrastructure.backup.v2.dto.V2BackupEntryDto
import de.christian2003.feature.export.infrastructure.backup.v2.dto.V2BackupTagDto
import de.christian2003.feature.export.infrastructure.backup.v2.mapper.V2BackupAccountMapper
import de.christian2003.feature.export.infrastructure.backup.v2.mapper.V2BackupDetailMapper
import de.christian2003.feature.export.infrastructure.backup.v2.mapper.V2BackupTagMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.FileInputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


internal class V2BackupService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val accountMapper: V2BackupAccountMapper,
    private val detailMapper: V2BackupDetailMapper,
    private val tagMapper: V2BackupTagMapper,
    private val createAccountsBatchUseCase: CreateAccountsBatchUseCase,
    private val filesystemService: FilesystemService,
    private val v2CipherService: V2CipherService
): ImportService {

    /**
     * CSV row divider in V2 backups is explicitly set to '\n' (no '\r\n' or other dynamics).
     * Prior to v3.8.0, this can be seen in
     * 'app\src\main\java\de\passwordvault\model\storage\csv\CsvConfiguration.java'
     */
    private val csvRowDivider: Char = '\n'

    /**
     * Custom CSV format which matches the CSV (de)serialization algorithm used when the V2 backup
     * was created.
     * Prior to v3.8.0, quotes were escaped with a backslash (i.e. '\"'). According to official CSV
     * specification, quotes are escaped with embedded double quotes (i.,e. '""'). However, prior to
     * v3.8.0, backslashes were used to escape quotes. Therefore, we need to use them here as well.
     */
    private val csvFormat: CSVFormat = CSVFormat.DEFAULT.builder()
        .setEscape('\\')
        .get()


    /**
     * Descriptor for the import service.
     */
    override val importDescriptor: ImportDescriptor = ImportDescriptor(
        id = "V2Backup",
        importFileExtensions = setOf("xml", "txt"),
        importFileMimeTypes = setOf("application/xml", "text/xml", "text/plain"),
        titleId = R.string.import_v3_title,
        subtitleId = R.string.import_v3_subtitle
    )


    /**
     * Performs a preflight check of the provided import file to get additional data about the
     * file to import.
     *
     * @param importFile                        URI of the file to import for which to perform the
     *                                          preflight request.
     * @return                                  Response containing properties of the file to import.
     * @throws CorruptImportFileException       The file to import is corrupt and cannot be imported.
     * @throws UnsupportedFileFormatException   The selected file is unsupported and cannot be imported.
     */
    override suspend fun getProperties(importFile: Uri): ImportProperties {
        //Check whether file extension is valid:
        val extension: String? = try {
            filesystemService.getExtension(importFile)
        } catch (_: Exception) { null }

        if (!importDescriptor.importFileExtensions.contains(extension)) {
            throw UnsupportedFileFormatException("The file format '${extension}' is not supported for V3 backups")
        }

        val document: Document = getDocument(importFile)
        val isEncrypted: Boolean = isEncrypted(document)

        val properties = ImportProperties(
            isEncrypted = isEncrypted
        )

        return properties
    }


    /**
     * Begins an import according to the provided configuration. The method starts a background
     * task and provides progress updates using the returned flow, which reports the progress as
     * a percentage between 0 % to 100 % (i.e. 0.0 to 1.0).
     *
     * @param config                            Configuration for the import.
     * @return                                  Flow which reports the progress.
     * @throws CorruptImportFileException       The file to import is corrupt and cannot be imported.
     * @throws IllegalImportConfigException     The import config is illegal.
     * @throws UnsupportedFileFormatException   The selected file is unsupported and cannot be imported.
     * @throws InvalidKeyException              The provided decryption key seed is invalid.
     */
    override fun beginImport(config: ImportConfig): Flow<Float> = flow {
        emit(0.0f) //Signal starting progress

        val document: Document = getDocument(config.importFile)
        val rootNode: Node = getRootNode(document)
        val isEncrypted: Boolean = isEncrypted(document)

        //Get data node:
        var dataNode: Node? = null
        for (i: Int in 0 until rootNode.childNodes.length) {
            val currentChild: Node = rootNode.childNodes.item(i)
            if (currentChild.nodeName.lowercase() == "data") {
                dataNode = currentChild
                break
            }
        }
        if (dataNode == null) {
            throw CorruptImportFileException("No <data> node inside XML document")
        }

        //Init cipher with key seed if required:
        if (isEncrypted) {
            if (config.decryptionKeySeed == null || config.decryptionKeySeed.isEmpty()) {
                throw IllegalImportConfigException("Missing decryption key seed")
            }
            v2CipherService.initCipher(config.decryptionKeySeed)

            //Check key validity:
            val checksumAttr: Node? = dataNode.attributes.getNamedItem("checksum")
            if (checksumAttr != null && !checksumAttr.nodeValue.isNullOrBlank()) {
                val checksum: String = checksumAttr.nodeValue
                var decryptedChecksum: String?
                var decryptedChecksumAsCharArray: CharArray? = null
                try {
                    decryptedChecksum = v2CipherService.decrypt(checksum)
                    decryptedChecksumAsCharArray = decryptedChecksum.toCharArray()
                    if (!decryptedChecksumAsCharArray.contentEquals(config.decryptionKeySeed)) {
                        //Invalid key seed:
                        throw InvalidKeyException("Decryption key seed invalid")
                    }
                }
                catch (_: Exception) {
                    throw InvalidKeyException("Decryption key seed invalid")
                }
                finally {
                    decryptedChecksumAsCharArray?.fill('\u0000')
                }
            }
        }

        var tagDtos: List<V2BackupTagDto>? = null
        var entryDtos: List<V2BackupEntryDto>? = null
        var detailDtos: List<V2BackupDetailDto>? = null

        //Traverse data:
        for (i: Int in 0 until dataNode.childNodes.length) {
            val currentChild: Node = dataNode.childNodes.item(i)

            when (currentChild.nodeName.lowercase()) {
                "tags" -> {
                    tagDtos = getTagsFromNode(currentChild, isEncrypted)
                }
                "entries" -> {
                    entryDtos = getEntriesFromNode(currentChild, isEncrypted)
                }
                "details" -> {
                    detailDtos = getDetailsFromNode(currentChild, isEncrypted)
                }
            }
        }

        if (tagDtos == null || entryDtos == null || detailDtos == null) {
            throw CorruptImportFileException("Data missing in XML file")
        }

        //Convert DTO to domain:
        val tags: List<Tag> = tagDtos
            .map { tagMapper.toDomain(it) }

        val details: Map<Uuid, List<Detail>> = detailDtos
            .groupBy { it.entryId }
            .mapValues { (key, value) ->
                val domainDetails: List<Detail> = value
                    .mapNotNull {
                        try {
                            detailMapper.toDomain(it)
                        }
                        catch (_: Exception) { null }
                    }
                return@mapValues domainDetails
            }

        val accounts: List<Account> = entryDtos
            .mapNotNull {
                try {
                    val details: List<Detail> = details[it.id] ?: emptyList()
                    val account: Account = accountMapper.toDomain(it, details, tags)
                    account
                }
                catch (_: Exception) { null }
            }

        //Create accounts:
        val batchOption: BatchOption = mapImportOptionToBatchOption(config.accountsImportOption)
        createAccountsBatchUseCase.createAccountsBatch(accounts, tags, batchOption)

        //Once finishing, make sure that progress reports 100 %:
        emit(1.0f)
    }


    /**
     * From the specified node (usually <tags>...</tags>), the method retrieves a list of tags.
     * The method decrypts the content of the node if required.
     * If a tag cannot be retrieved, it may be skipped.
     *
     * @param node          V2 backup node which contains the tags.
     * @param isEncrypted   Whether the backup is encrypted.
     * @return              List of tags.
     */
    private fun getTagsFromNode(node: Node, isEncrypted: Boolean): List<V2BackupTagDto> {
        val tagDtos: MutableList<V2BackupTagDto> = mutableListOf()
        var tagsCsv: String? = node.textContent

        if (tagsCsv != null) {
            //Decrypt if required:
            if (isEncrypted) {
                tagsCsv = v2CipherService.decrypt(tagsCsv)
            }

            //Parse tags:
            var parser: CSVParser? = null
            try {
                /*
                Format:  '<id>                                ,<name>'
                Example: '4526e96a-69cb-4a37-91fc-a002997dfd3e,Private'
                 */
                parser = CSVParser.parse(tagsCsv, csvFormat)

                for (record: CSVRecord in parser) {
                    try {
                        //Get string values from record:
                        val idString: String? = record.get(0)
                        val nameString: String? = record.get(1)

                        //Parse string values:
                        val id: Uuid = UUID.fromString(idString).toKotlinUuid()

                        //Validate data and create DTO:
                        if (!nameString.isNullOrBlank()) {
                            val tagDto = V2BackupTagDto(
                                id = id,
                                name = nameString
                            )
                            tagDtos.add(tagDto)
                        }
                    }
                    catch (_: Exception) {
                        //Something went wrong: Skip tag (partial import)
                    }
                }
            }
            catch (_: Exception) {
                throw CorruptImportFileException("Cannot parse tags")
            }
            finally {
                parser?.close()
            }
        }

        return tagDtos
    }


    /**
     * From the specified node (usually <entries>...</entries>), the method retrieves a list of entries.
     * The method decrypts the content of the node if required.
     * If an entry cannot be retrieved, it may be skipped.
     *
     * @param node          V2 backup node which contains the entries.
     * @param isEncrypted   Whether the backup is encrypted.
     * @return              List of entries.
     */
    private fun getEntriesFromNode(node: Node, isEncrypted: Boolean): List<V2BackupEntryDto> {
        val entryDtos: MutableList<V2BackupEntryDto> = mutableListOf()
        val entriesCsv: String? = node.textContent

        var parser: CSVParser? = null

        if (entriesCsv != null) {
            val rows: List<String> = entriesCsv
                .split(csvRowDivider)
                .filter { it.isNotBlank() }

            for (row: String in rows) {
                var rowCsv: String = row

                //Decrypt if required:
                if (isEncrypted) {
                    rowCsv = v2CipherService.decrypt(rowCsv)
                }

                //Parse entries:
                try {
                    /*
                    Format   '<id>                                ,<name>      ,<description>,<created>    ,<edited>     ,<is_visible>,"<tag1>,<tag2>,<tagN>"                                                     ,"<package1>,<package2>,<packageN>",<added_automatically>':
                    Example: '2f175ffb-ada5-42da-839c-ce15718f8497,Petrol Index,Petrol usages,1786992277500,1786992334885,true        ,"4526e96a-69cb-4a37-91fc-a002997dfd3e,eef6e60b-8c5e-4d35-a4a8-dca895555b70","de.christian2003.petrolindex"    ,false'
                     */
                    parser = CSVParser.parse(rowCsv, csvFormat)

                    for (record: CSVRecord in parser) {
                        try {
                            //Get string values from record:
                            val idString: String? = record.get(0)
                            val nameString: String? = record.get(1)
                            val descriptionString: String? = record.get(2)
                            val createdString: String? = record.get(3)
                            val editedString: String? = record.get(4)
                            val isVisibleString: String? = record.get(5)
                            val tagsString: String? = record.get(6)
                            val packagesString: String? = record.get(7)
                            val isAddedAutomaticallyString: String? = record.get(8)

                            //Parse string values:
                            val id: Uuid = UUID.fromString(idString).toKotlinUuid()

                            val created: LocalDateTime = if (createdString != null) {
                                Instant.ofEpochMilli(createdString.toLong()).atZone(ZoneOffset.UTC).toLocalDateTime()
                            } else { LocalDateTime.now() }

                            val edited: LocalDateTime = if (editedString != null) {
                                Instant.ofEpochMilli(editedString.toLong()).atZone(ZoneOffset.UTC).toLocalDateTime()
                            } else { LocalDateTime.now() }

                            val isVisible: Boolean = isVisibleString.toBoolean()

                            val tags: List<Uuid> = tagsString
                                ?.split(',')
                                ?.filter { it.isNotBlank() }
                                ?.map { UUID.fromString(it).toKotlinUuid() }
                                ?: emptyList()

                            val packages: List<String> = packagesString
                                ?.split(',')
                                ?: emptyList()

                            val isAddedAutomatically: Boolean = isAddedAutomaticallyString.toBoolean()

                            //Validate data and create DTO:
                            if (!nameString.isNullOrBlank()) {
                                val entryDto = V2BackupEntryDto(
                                    id = id,
                                    name = nameString,
                                    description = descriptionString,
                                    created = created,
                                    edited = edited,
                                    isVisible = isVisible,
                                    tagIds = tags,
                                    packages = packages,
                                    isAddedAutomatically = isAddedAutomatically
                                )
                                entryDtos.add(entryDto)
                            }
                        }
                        catch (_: Exception) {
                            //Something went wrong: Skip entry (partial import)
                        }
                    }
                }
                catch (_: Exception) {
                    //Something went wrong: Skip entry (partial import)
                }
                finally {
                    parser?.close()
                }
            }
        }

        return entryDtos
    }


    /**
     * From the specified node (usually <details>...</details>), the method retrieves a list of details.
     * The method decrypts the content of the node if required.
     * If a detail cannot be retrieved, it may be skipped.
     *
     * @param node          V2 backup node which contains the details.
     * @param isEncrypted   Whether the backup is encrypted.
     * @return              List of details.
     */
    private fun getDetailsFromNode(node: Node, isEncrypted: Boolean): List<V2BackupDetailDto> {
        val detailDtos: MutableList<V2BackupDetailDto> = mutableListOf()
        val detailsCsv: String? = node.textContent

        var parser: CSVParser? = null

        if (detailsCsv != null) {
            val rows: List<String> = detailsCsv.split(csvRowDivider)

            for (row: String in rows) {
                var rowCsv: String = row

                //Decrypt if required:
                if (isEncrypted) {
                    rowCsv = v2CipherService.decrypt(rowCsv)
                }

                //Parse details:
                try {
                    /*
                    Format:  '<id>                                ,<entry>,                            ,<name>  ,<content>    ,<created>    ,<edited>     ,<type>,<is_visible>,<is_obfuscated>,<is_username>,<is_password>'
                    Example: '1a6c52e6-4486-460c-83d6-f4e305d1f4d3,2f175ffb-ada5-42da-839c-ce15718f8497,Password,MyPassword123,1786992321809,1786992327359,0     ,true        ,false          ,false        ,false'
                     */
                    parser = CSVParser.parse(rowCsv, csvFormat)

                    for (record: CSVRecord in parser) {
                        try {
                            //Get string values from record:
                            val idString: String? = record.get(0)
                            val entryString: String? = record.get(1)
                            val nameString: String? = record.get(2)
                            val contentString: String? = record.get(3)
                            val createdString: String? = record.get(4)
                            val editedString: String? = record.get(5)
                            val typeIdString: String? = record.get(6)
                            val isVisibleString: String? = record.get(7)
                            val isObfuscatedString: String? = record.get(8)
                            val isUsernameString: String? = record.get(9)
                            val isPasswordString: String? = record.get(10)

                            //Parse string values:
                            val id: Uuid = UUID.fromString(idString).toKotlinUuid()

                            val entryId: Uuid = UUID.fromString(entryString).toKotlinUuid()

                            val created: LocalDateTime = if (createdString != null) {
                                Instant.ofEpochMilli(createdString.toLong()).atZone(ZoneOffset.UTC).toLocalDateTime()
                            } else { LocalDateTime.now() }

                            val edited: LocalDateTime = if (editedString != null) {
                                Instant.ofEpochMilli(editedString.toLong()).atZone(ZoneOffset.UTC).toLocalDateTime()
                            } else { LocalDateTime.now() }

                            val typeId: Int = typeIdString?.toInt() ?: 0

                            val isVisible: Boolean = isVisibleString.toBoolean()

                            val isObfuscated: Boolean = isObfuscatedString.toBoolean()

                            val isUsername: Boolean = isUsernameString.toBoolean()

                            val isPassword: Boolean = isPasswordString.toBoolean()

                            //Validate data and create DTO:
                            if (!nameString.isNullOrBlank() && !contentString.isNullOrBlank()) {
                                val detailDto = V2BackupDetailDto(
                                    id = id,
                                    entryId = entryId,
                                    name = nameString,
                                    content = contentString,
                                    created = created,
                                    edited = edited,
                                    typeId = typeId,
                                    isVisible = isVisible,
                                    isObfuscated = isObfuscated,
                                    isUsername = isUsername,
                                    isPassword = isPassword
                                )
                                detailDtos.add(detailDto)
                            }
                        }
                        catch (_: Exception) {
                            //Something went wrong: Skip tag (partial import)
                        }
                    }
                }
                catch (_: Exception) {
                    //Something went wrong: Skip entry (partial import)
                }
                finally {
                    parser?.close()
                }
            }
        }

        return detailDtos
    }


    /**
     * Retrieves and parses the XML file of the specified URI.
     *
     * @param importFile                    URI of the XML file to parse.
     * @return                              Document representing the XML file.
     * @throws CorruptImportFileException   The XML file is corrupt.
     */
    private fun getDocument(importFile: Uri): Document {
        var document: Document? = null
        context.contentResolver.openFileDescriptor(importFile, "r")?.use { parcelFileDescriptor ->
            FileInputStream(parcelFileDescriptor.fileDescriptor).use { fileInputStream ->
                try {
                    val documentBuilderFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
                    documentBuilderFactory.isNamespaceAware = false
                    documentBuilderFactory.isValidating = false

                    val documentBuilder: DocumentBuilder = documentBuilderFactory.newDocumentBuilder()

                    document = documentBuilder.parse(fileInputStream)
                }
                catch (_: Exception) { }
            }
        }

        if (document == null) {
            throw CorruptImportFileException("The XML document is corrupt and cannot be parsed")
        }

        return document
    }


    /**
     * Returns the root node (i.e. "<password_vault>") from the XML document.
     *
     * @param document                      Document whose root node to return.
     * @return                              Root node.
     * @throws CorruptImportFileException   Too many or too few root nodes.
     */
    private fun getRootNode(document: Document): Node {
        val children: NodeList = document.getElementsByTagName("password_vault")
        if (children.length > 1) {
            throw CorruptImportFileException("Multiple password_vault nodes")
        }
        else if (children.length == 0) {
            throw CorruptImportFileException("No password_vault root node")
        }

        return children.item(0)
    }


    /**
     * Checks whether the XML document is encrypted.
     *
     * @param document  Document to check.
     * @return          Whether the document is encrypted.
     */
    private fun isEncrypted(document: Document): Boolean {
        var isEncrypted = false
        val rootNode: Node = getRootNode(document)
        val documentChildren: NodeList? = rootNode.childNodes

        if (documentChildren != null) {
            for (i: Int in 0 until documentChildren.length) {
                val currentChild: Node = documentChildren.item(i)
                when (currentChild.nodeName.lowercase()) {
                    "data" -> {
                        val checksumAttr: Node? = currentChild.attributes.getNamedItem("checksum")
                        if (checksumAttr != null && !checksumAttr.nodeValue.isNullOrBlank()) {
                            isEncrypted = true
                        }
                    }
                }
            }
        }

        return isEncrypted
    }


    /**
     * Maps the import option selected by the user to the corresponding batch option required
     * to create the batch of accounts for the import.
     *
     * @param importOption  Import option to map.
     * @return              Corresponding batch option.
     */
    private fun mapImportOptionToBatchOption(importOption: ImportOption): BatchOption {
        return when (importOption) {
            ImportOption.SkipDuplicates -> BatchOption.SkipDuplicates
            ImportOption.OverrideDuplicates -> BatchOption.ReplaceDuplicates
            ImportOption.DeleteAllExistingData -> BatchOption.DeleteAllExisting
        }
    }

}
