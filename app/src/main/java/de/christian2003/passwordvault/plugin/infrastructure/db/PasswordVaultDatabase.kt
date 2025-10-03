package de.christian2003.passwordvault.plugin.infrastructure.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.christian2003.passwordvault.plugin.infrastructure.db.converter.LocalDateTimeConverter
import de.christian2003.passwordvault.plugin.infrastructure.db.converter.UuidConverter
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.DetailDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.AccountDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.TagDao
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.DetailEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.AccountEntity
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.AccountTagCrossRef
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TagEntity


@Database(
    entities = [
        AccountEntity::class,
        DetailEntity::class,
        TagEntity::class,
        AccountTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    UuidConverter::class,
    LocalDateTimeConverter::class
)
abstract class PasswordVaultDatabase(): RoomDatabase() {

    /**
     * DAO through which to access the entries.
     */
    abstract val accountDao: AccountDao

    /**
     * DAO through which to access the details.
     */
    abstract val detailDao: DetailDao

    /**
     * DAO through which to access the tags.
     */
    abstract val tagDao: TagDao


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
