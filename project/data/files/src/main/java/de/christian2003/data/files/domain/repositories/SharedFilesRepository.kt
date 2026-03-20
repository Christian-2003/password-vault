package de.christian2003.data.files.domain.repositories

import java.time.LocalDateTime


internal interface SharedFilesRepository {

    fun getSharedFiles(): Map<String, LocalDateTime>

    fun addSharedFile(sharedFileName: String)

    fun removeSharedFiles(sharedFileNames: List<String>)

}
