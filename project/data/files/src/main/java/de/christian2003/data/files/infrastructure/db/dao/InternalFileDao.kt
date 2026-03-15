package de.christian2003.data.files.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.christian2003.data.files.infrastructure.db.entities.InternalFileEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface InternalFileDao {

    @Query("SELECT * FROM files_lookup WHERE internalName = :internalName")
    suspend fun selectInternalFileByInternalName(internalName: String): InternalFileEntity

    @Query("SELECT * FROM files_lookup WHERE internalName IN (:internalNames)")
    fun selectInternalFilesByInternalNames(internalNames: List<String>): Flow<List<InternalFileEntity>>

    @Insert
    suspend fun insert(internalFile: InternalFileEntity)

    @Delete
    suspend fun delete(internalFile: InternalFileEntity)

}
