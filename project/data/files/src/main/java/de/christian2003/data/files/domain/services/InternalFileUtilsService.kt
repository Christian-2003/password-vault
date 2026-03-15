package de.christian2003.data.files.domain.services


internal interface InternalFileUtilsService {

    fun getSizeOfInternalFile(internalFilePath: String): Long

    fun getAbsolutePathForInternalFile(internalFilePath: String): String

}
