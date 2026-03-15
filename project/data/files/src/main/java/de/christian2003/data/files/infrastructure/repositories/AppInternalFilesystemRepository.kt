package de.christian2003.data.files.infrastructure.repositories

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import de.christian2003.data.files.domain.services.FileCopyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.uuid.Uuid


@Singleton
internal class AppInternalFilesystemRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val fileCopyService: FileCopyService
): InternalFilesystemRepository {

    /**
     * Absolute path to the root directory of the user files:
     * ".../data/data/de.christian2003.passwordvault/files/userfiles"
     */
    private val absoluteInternalDirPath: File = File(context.filesDir, "userfiles")

    /**
     * Flows for subdirectories within a directory:
     * key = internal directory path        value = all subdirectories
     */
    private val subDirectoriesFlows: MutableMap<String, MutableStateFlow<List<InternalDirectory>>> = mutableMapOf()

    /**
     * Flows for files within a directory:
     * key = internal directory path        value = all files in internal directory
     */
    private val filesInDirectoriesFlows: MutableMap<String, MutableStateFlow<List<String>>> = mutableMapOf()


    override fun getAllSubdirectories(directory: InternalDirectory): Flow<List<InternalDirectory>> {
        val key = directory.internalPath
        return subDirectoriesFlows.getOrPut(key) {
            MutableStateFlow(scanSubDirs(directory))
        }
    }


    override fun addDirectory(directory: InternalDirectory) {
        val absoluteDirPath: File = getAbsolutePathForDir(directory.internalPath)

        if (!absoluteDirPath.exists()) {
            absoluteDirPath.mkdirs()
            emitDirParentUpdate(directory.internalPath)
        }
    }


    override fun updateDirectory(currentInternalPath: String, updatedDirectory: InternalDirectory) {
        val currentAbsoluteDirPath: File = getAbsolutePathForDir(currentInternalPath)
        val newAbsoluteDirPath: File = getAbsolutePathForDir(updatedDirectory.internalPath)

        if (currentAbsoluteDirPath.exists()) {
            currentAbsoluteDirPath.renameTo(newAbsoluteDirPath)

            emitDirParentUpdate(currentInternalPath)
            emitDirParentUpdate(updatedDirectory.internalPath)

            //Move the flow for the updated directory:
            subDirectoriesFlows[currentInternalPath]?.let { flow ->
                subDirectoriesFlows.remove(currentInternalPath)
                subDirectoriesFlows[updatedDirectory.internalPath] = flow
            }
        }
    }


    override fun deleteDirectory(directory: InternalDirectory) {
        val absoluteDirPath: File = getAbsolutePathForDir(directory.internalPath)

        if (absoluteDirPath.exists()) {
            absoluteDirPath.deleteRecursively()
            emitDirParentUpdate(directory.internalPath)
        }
    }


    override fun getAllFileNamesInDirectory(directory: InternalDirectory): Flow<List<String>> {
        val key = directory.internalPath
        return filesInDirectoriesFlows.getOrPut(key) {
            MutableStateFlow(scanDirForFiles(directory))
        }
    }

    override suspend fun copyFileToDirectory(sourceFileUri: Uri, destinationFileName: String, directory: InternalDirectory): String {
        val internalFilePath = directory.internalPath + "/" + destinationFileName

        val originalFileName: String = fileCopyService.copyExternalFileToInternal(sourceFileUri, internalFilePath)
        emitDirFilesUpdate(directory)

        return originalFileName
    }

    override fun deleteFileFromDirectory(fileName: String, directory: InternalDirectory) {
        val internalFilePath = directory.internalPath + "/" + fileName
        val absoluteFilePath: File = getAbsolutePathForDir(internalFilePath)

        if (absoluteFilePath.exists()) {
            absoluteFilePath.delete()
            emitDirFilesUpdate(directory)
        }
    }



    private fun getAbsolutePathForDir(internalPath: String): File {
        return File(absoluteInternalDirPath, internalPath)
    }


    private fun emitDirFilesUpdate(directory: InternalDirectory) {
        val flow: MutableStateFlow<List<String>>? = filesInDirectoriesFlows[directory.internalPath]
        if (flow != null) {
            flow.value = scanDirForFiles(directory)
        }
    }

    private fun emitDirParentUpdate(internalPath: String) {
        val internalParentPath: String = File(internalPath).parent?.trim('/') ?: ""
        val internalParentDir = InternalDirectory(internalParentPath)

        val flow: MutableStateFlow<List<InternalDirectory>>? = subDirectoriesFlows[internalParentPath]
        if (flow != null) {
            flow.value = scanSubDirs(internalParentDir)
        }
    }


    private fun scanSubDirs(directory: InternalDirectory): List<InternalDirectory> {
        val absoluteDirPath: File = getAbsolutePathForDir(directory.internalPath)
        val absoluteSubDirs: List<File> = absoluteDirPath.listFiles()?.filter { it.isDirectory } ?: listOf()

        val internalSubDirs: List<InternalDirectory> = absoluteSubDirs.map { absoluteSubDir ->
            InternalDirectory(absoluteSubDir.relativeTo(absoluteInternalDirPath).path)
        }
        return internalSubDirs
    }


    private fun scanDirForFiles(directory: InternalDirectory): List<String> {
        val absoluteDirPath: File = getAbsolutePathForDir(directory.internalPath)
        val absoluteFiles: List<File> = absoluteDirPath.listFiles()?.filter { it.isFile } ?: listOf()

        val fileNames: List<String> = absoluteFiles.map { absoluteFile ->
            absoluteFile.name
        }
        return fileNames
    }

}
