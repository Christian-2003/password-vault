package de.christian2003.data.files.infrastructure.repositories

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.data.files.domain.repositories.SharedFilesRepository
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
     * "<"filename 1>";<timestamp 1>
     * "<filename 2>";<timestamp 2>
     * "<filename 3>";<timestamp 3>
     */
    private val dataFileName: File = File(context.filesDir, "sharedFiles.csv")

    private val format: CSVFormat = CSVFormat.DEFAULT.builder()
        .setDelimiter(';')
        .setQuote('"')
        .setTrim(true)
        .get()


    override fun getSharedFiles(): Map<String, LocalDateTime> {
        val sharedFiles: MutableMap<String, LocalDateTime> = mutableMapOf()

        try {
            dataFileName.reader().use { reader ->
                val records: CSVParser = format.parse(reader)

                records.forEach { record ->
                    val fileName: String = record.get(0)
                    val epochSecond: Long = record.get(1).toLongOrNull() ?: 0
                    val timestamp: LocalDateTime = LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.UTC)

                    sharedFiles.put(fileName, timestamp)
                }
            }
        }
        catch (e: Exception) {
            Log.e("Filesystem", "Cannot read shared files: ${e.message ?: "Unknown error"}")
        }

        return sharedFiles
    }


    override fun addSharedFile(sharedFileName: String) {
        val sharedFiles: MutableMap<String, LocalDateTime> = getSharedFiles().toMutableMap()

        val timestamp: LocalDateTime = LocalDateTime.now()
        sharedFiles.put(sharedFileName, timestamp)

        writeToFile(sharedFiles)
    }


    override fun removeSharedFiles(sharedFileNames: List<String>) {
        val sharedFiles: MutableMap<String, LocalDateTime> = getSharedFiles().toMutableMap()

        sharedFileNames.forEach { sharedFileName ->
            if (sharedFiles.contains(sharedFileName)) {
                sharedFiles.remove(sharedFileName)
            }
        }

        writeToFile(sharedFiles)
    }


    private fun writeToFile(sharedFiles: Map<String, LocalDateTime>) {
        try {
            dataFileName.writer().use { writer ->
                CSVPrinter(writer, format).use { printer ->
                    sharedFiles.forEach { fileName, timestamp ->
                        val epochSecond: Long = timestamp.toEpochSecond(ZoneOffset.UTC)
                        printer.printRecord(fileName, epochSecond)
                    }
                }
            }
        }
        catch (e: Exception) {
            Log.e("Filesystem", "Cannot add shared files: ${e.message ?: "Unknown error"}")
        }
    }

}
