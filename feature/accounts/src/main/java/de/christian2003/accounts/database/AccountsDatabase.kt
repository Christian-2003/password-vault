package de.christian2003.accounts.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [AccountEntity::class], version = 1, exportSchema = false)
@TypeConverters(UUIDConverter::class)
abstract class AccountsDatabase: RoomDatabase() {

    abstract val accountDao: AccountDao


    companion object {

        @Volatile
        private var INSTANCE: AccountsDatabase? = null


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
