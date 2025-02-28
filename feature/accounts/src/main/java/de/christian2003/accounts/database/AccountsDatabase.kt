package de.christian2003.accounts.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


/**
 * Class implements the database through which to access all data relevant to the accounts.
 *
 * @author  Christian-2003
 * @since   3.8.0
 */
@Database(entities = [AccountEntity::class, DetailEntity::class], version = 3, exportSchema = false)
@TypeConverters(UUIDConverter::class, LocalDateTimeConverter::class)
abstract class AccountsDatabase: RoomDatabase() {

    /**
     * DAO through which to access the accounts.
     */
    abstract val accountDao: AccountDao

    /**
     * DAO through which to access the account details.
     */
    abstract val detailDao: DetailDao


    companion object {

        /**
         * Singleton instance of the database.
         */
        @Volatile
        private var INSTANCE: AccountsDatabase? = null


        /**
         * Returns the singleton instance of the database.
         *
         * @param context   Context from which to create the database.
         * @return          Database
         */
        fun getInstance(context: Context): AccountsDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context = context.applicationContext,
                        klass = AccountsDatabase::class.java,
                        name = "accounts_database"
                    ).fallbackToDestructiveMigration().build()
                }
                return instance
            }
        }

    }

}
