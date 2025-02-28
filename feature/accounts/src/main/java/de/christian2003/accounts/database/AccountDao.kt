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
 * Class implements a data access object through which to access accounts within the room database.
 *
 * @author  Christian-2003
 * @since   3.8.0
 */
@Dao
interface AccountDao {

    /**
     * Returns a list of all accounts.
     *
     * @return  List of all accounts.
     */
    @Query("SELECT * FROM accounts")
    fun selectAll(): Flow<List<AccountEntity>>


    /**
     * Returns an account of the ID specified. If no account exists with the ID specified,
     * null is returned.
     *
     * @param id    ID of the account to return.
     * @return      Account of the ID specified or null if no account exists.
     */
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun selectById(id: UUID): AccountEntity?


    /**
     * Inserts the account specified into the database. If the account already exists, it is replaced
     * with the account passed.
     *
     * @param account   Account to insert into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity)


    /**
     * Deletes the account specified.
     *
     * @param account   Account to delete.
     */
    @Delete
    suspend fun delete(account: AccountEntity)


    /**
     * Updates the account within the database. The account to update is determined based on it's
     * primary key (i.e. it's UUID).
     *
     * @param account   Account to update.
     */
    @Update
    suspend fun update(account: AccountEntity)

}
