package de.christian2003.data.files.infrastructure.repositories

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.repositories.SharedFilesRepository
import de.christian2003.data.files.domain.entities.SharedFileMetadata
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject


internal class SharedFilesRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
): SharedFilesRepository {

    /**
     * This file stores a list of all files that are currently being shared in the following format:
     * <actual filename 1>;<internal filename 1>;<timestamp 1>
     * <actual filename 2>;<internal filename 2>;<timestamp 2>
     * <actual filename 3>;<internal filename 3>;<timestamp 3>
     */
    private val metadataFile: File = File(context.filesDir, "sharedFiles.csv")

    private val format: CSVFormat = CSVFormat.DEFAULT.builder()
        .setDelimiter(';')
        .setQuote('"')
        .setTrim(true)
        .get()


    override fun getSharedFiles(): List<SharedFileMetadata> {
        val sharedFiles: MutableList<SharedFileMetadata> = mutableListOf()

        try {
            metadataFile.reader().use { reader ->
                val records: CSVParser = format.parse(reader)

                records.forEach { record ->
                    val actualName: String = record.get(0)
                    val internalName: String = record.get(1)
                    val epochSecond: Long = record.get(2).toLongOrNull() ?: 0
                    val timestamp: LocalDateTime = LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC)

                    val sharedFileMetadata = SharedFileMetadata(
                        actualFileName = actualName,
                        internalFileName = internalName,
                        timestamp = timestamp
                    )
                    sharedFiles.add(sharedFileMetadata)
                }
            }
        }
        catch (e: Exception) {
            Log.e("Filesystem", "Cannot read shared files: ${e.message ?: "Unknown error"}")
        }

        return sharedFiles
    }


    override fun addSharedFile(file: InternalFile): String {
        val sharedFiles: MutableList<SharedFileMetadata> = getSharedFiles().toMutableList()

        val matchingSharedFile: SharedFileMetadata? = sharedFiles.firstOrNull { sharedFile -> sharedFile.internalFileName == file.internalName }
        val timestamp: LocalDateTime = LocalDateTime.now()

        val sharedFile: SharedFileMetadata = if (matchingSharedFile != null) {
            sharedFiles.remove(matchingSharedFile)
            matchingSharedFile.copy(
                timestamp = timestamp
            )
        } else {
            SharedFileMetadata(
                actualFileName = file.actualFileName,
                internalFileName = file.internalName,
                timestamp = timestamp
            )
        }

        sharedFiles.add(sharedFile)

        writeToFile(sharedFiles)

        return sharedFile.actualFileName
    }


    override fun removeSharedFiles(internalFileNames: List<String>) {
        val sharedFiles: MutableList<SharedFileMetadata> = getSharedFiles().toMutableList()

        internalFileNames.forEach { internalFileName ->
            val matchingSharedFile: SharedFileMetadata? = sharedFiles.firstOrNull { sf -> sf.internalFileName == internalFileName }
            if (matchingSharedFile != null) {
                sharedFiles.remove(matchingSharedFile)
            }
        }

        writeToFile(sharedFiles)
    }


    private fun writeToFile(sharedFiles: List<SharedFileMetadata>) {
        try {
            metadataFile.writer().use { writer ->
                CSVPrinter(writer, format).use { printer ->
                    sharedFiles.forEach { sharedFile ->
                        val epochSecond: Long = sharedFile.timestamp.toEpochSecond(ZoneOffset.UTC)
                        printer.printRecord(sharedFile.actualFileName, sharedFile.internalFileName, epochSecond)
                    }
                }
            }
        }
        catch (e: Exception) {
            Log.e("Filesystem", "Cannot add shared files: ${e.message ?: "Unknown error"}")
        }
    }

}
