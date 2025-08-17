package de.christian2003.passwordvault.plugin.infrastructure.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.christian2003.passwordvault.plugin.infrastructure.db.converter.UuidConverter
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.EntryDao
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.EntryEntity


@Database(
    entities = [
        EntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    UuidConverter::class
)
abstract class PasswordVaultDatabase(): RoomDatabase() {

    /**
     * DAO through which to access the entries.
     */
    abstract val entryDao: EntryDao


    companion object {

        /**
         * Database singleton instance.
         */
        @Volatile
        private var INSTANCE: PasswordVaultDatabase? = null


        /**
         * Returns the singleton instance of the database.
         *
         * @param context   Context from which to create the database if none exists already.
         * @return          Database singleton instance.
         */
        fun getInstance(context: Context): PasswordVaultDatabase {
            synchronized(this) {
                var instance: PasswordVaultDatabase? = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context = context.applicationContext,
                        klass = PasswordVaultDatabase::class.java,
                        name = "password_vault_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return instance
            }
        }

    }

}
