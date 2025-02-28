package de.christian2003.accounts.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID


/**
 * Class implements a data access object through which to access details within the database.
 *
 * @author  Christian-2003
 * @since   3.8.0
 */
@Dao
interface DetailDao {

    /**
     * Returns all details for the account specified.
     *
     * @param account   ID of the account whose details to return.
     * @return          List of details associated to the account specified.
     */
    @Query("SELECT * FROM details WHERE account = :account")
    fun selectForAccount(account: UUID): Flow<List<DetailEntity>>


    /**
     * Returns the detail with the ID specified. If no detail with this ID exists, null is returned.
     *
     * @param id    ID of the detail to return.
     * @return      Detail with the ID specified or null if no detail with the ID exists.
     */
    @Query("SELECT * FROM details WHERE id = :id")
    suspend fun selectById(id: UUID): DetailEntity?


    /**
     * Inserts the detail specified into the database. If the detail already exists, it is replaced.
     *
     * @param detail    Detail to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(detail: DetailEntity)


    /**
     * Deletes the detail specified from the database.
     *
     * @param detail    Detail to delete.
     */
    @Delete
    suspend fun delete(detail: DetailEntity)


    /**
     * Updates the detail specified within the database. The detail to update is determined by it's
     * primary key (i.e. it's UUID).
     *
     * @param detail    Detail to update.
     */
    @Update
    suspend fun update(detail: DetailEntity)

}
