package de.christian2003.data.accounts.di

import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.christian2003.data.accounts.infrastructure.db.PasswordVaultDatabase
import de.christian2003.data.accounts.infrastructure.db.dao.AccountDao
import de.christian2003.data.accounts.infrastructure.db.dao.DetailDao
import de.christian2003.data.accounts.infrastructure.db.dao.TagDao
import de.christian2003.data.accounts.infrastructure.db.dao.TargetDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal class AccountProviderSingletonModule {

    //========================= INFRASTRUCTURE DATABASE =========================

    @Provides
    @Singleton
    fun providePasswordVaultDatabase(
        @ApplicationContext context: Context
    ): PasswordVaultDatabase {
        return PasswordVaultDatabase.getInstance(context)
    }

    @Provides
    fun provideAccountDao(db: PasswordVaultDatabase): AccountDao {
        return db.accountDao
    }

    @Provides
    fun provideDetailDao(db: PasswordVaultDatabase): DetailDao {
        return db.detailDao
    }

    @Provides
    fun provideTagDao(db: PasswordVaultDatabase): TagDao {
        return db.tagDao
    }

    @Provides
    fun provideTargetDao(db: PasswordVaultDatabase): TargetDao {
        return db.targetDao
    }


    //========================= ANDROID COMPONENTS =========================

    @Provides
    @Singleton
    fun providePackageManager(
        @ApplicationContext context: Context
    ): PackageManager {
        return context.packageManager
    }

}
