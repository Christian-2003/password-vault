package de.christian2003.data.files.infrastructure.repositories

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.repositories.InternalFilesystemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
internal class AppInternalFilesystemRepository @Inject constructor(
    @ApplicationContext private val context: Context
): InternalFilesystemRepository {

    /**
     * Absolute path to the root directory of the user files:
     * ".../data/data/de.christian2003.passwordvault/files/userfiles"
     */
    private val absoluteInternalDirPath: File = File(context.filesDir, "userfiles")

    private val directoryFlows: MutableMap<String, MutableStateFlow<List<InternalDirectory>>> = mutableMapOf()


    override fun getAllSubdirectories(directory: InternalDirectory): Flow<List<InternalDirectory>> {
        val key = directory.internalPath
        return directoryFlows.getOrPut(key) {
            MutableStateFlow(scanSubDirs(directory))
        }
    }


    override fun addDirectory(directory: InternalDirectory) {
        Log.d("Files", "Repository start creation of '${directory.internalPath}'")
        val absoluteDirPath: File = getAbsolutePathForDir(directory)

        if (!absoluteDirPath.exists()) {
            absoluteDirPath.mkdirs()
            emitDirParentUpdate(directory)
            Log.d("Files", "Added directory '${directory.internalPath}'")
        }
        else {
            Log.d("Files", "'${absoluteDirPath.path}' is no directory or exists")
        }
        Log.d("Files", "Repository finish creation of '${directory.internalPath}'")
    }


    override fun deleteDirectory(directory: InternalDirectory) {
        val absoluteDirPath: File = getAbsolutePathForDir(directory)

        if (absoluteDirPath.exists()) {
            absoluteDirPath.deleteRecursively()
            emitDirParentUpdate(directory)
            Log.d("Files", "Deleted directory '${directory.internalPath}'")
        }
    }


    private fun getAbsolutePathForDir(directory: InternalDirectory): File {
        val internalDirPath = directory.internalPath //directory.internalPath always begins with "/"

        return File(absoluteInternalDirPath, internalDirPath)
    }


    private fun emitDirParentUpdate(directory: InternalDirectory) {
        val internalParentPath: String = File(directory.internalPath).parent?.trim('/') ?: ""
        val internalParentDir = InternalDirectory(internalParentPath)

        val flow: MutableStateFlow<List<InternalDirectory>>? = directoryFlows[internalParentPath]
        if (flow != null) {
            flow.value = scanSubDirs(internalParentDir)
        }
    }


    private fun scanSubDirs(directory: InternalDirectory): List<InternalDirectory> {
        val absoluteDirPath: File = getAbsolutePathForDir(directory)
        val absoluteSubDirs: List<File> = absoluteDirPath.listFiles()?.filter { it.isDirectory } ?: listOf()

        val internalSubDirs: List<InternalDirectory> = absoluteSubDirs.map { absoluteSubDir ->
            InternalDirectory(absoluteSubDir.relativeTo(absoluteInternalDirPath).path)
        }
        return internalSubDirs
    }

}
