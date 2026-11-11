package de.christian2003.filesysteminspector.ui.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import de.christian2003.filesysteminspector.application.FileMetadataProvider
import de.christian2003.filesysteminspector.domain.FileMetadata
import org.w3c.dom.Document


class MainViewModel(
    application: Application
): AndroidViewModel(application) {

    var rootUri: Uri? by mutableStateOf(null)

    val directoryStack: MutableList<DocumentFile> = mutableStateListOf()

    val files: List<DocumentFile> by derivedStateOf {
        val currentDir: DocumentFile? = directoryStack.lastOrNull()
        if (currentDir != null) {
            return@derivedStateOf currentDir
                .listFiles()
                .sortedWith(compareBy<DocumentFile>({ !it.isDirectory }, { it.name?.lowercase() ?: "" }))
        }
        else {
            return@derivedStateOf emptyList()
        }
    }

    val currentDirectoryName: String by derivedStateOf {
        val currentDir: DocumentFile? = directoryStack.lastOrNull()
        return@derivedStateOf currentDir?.name ?: "FilesystemInspector"
    }

    var selectedFile: FileMetadata? by mutableStateOf(null)


    fun addUriToStack(uri: Uri) {
        val dir: DocumentFile? = DocumentFile.fromTreeUri(application, uri)
        if (dir != null) {
            directoryStack.add(dir)
        }
    }


    fun navigateUpDirectory() {
        if (directoryStack.size > 1) {
            directoryStack.removeAt(directoryStack.size - 1)
        }
    }


    fun selectFile(file: DocumentFile) {
        val contentResolver: ContentResolver = application.contentResolver
        val provider = FileMetadataProvider()

        selectedFile = provider.getMetadata(file, contentResolver)
    }

}
