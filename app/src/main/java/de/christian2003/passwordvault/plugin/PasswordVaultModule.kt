package de.christian2003.passwordvault.plugin

import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.christian2003.passwordvault.application.repository.AccountRepository
import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.application.repository.PackagesRepository
import de.christian2003.passwordvault.application.repository.TagRepository
import de.christian2003.passwordvault.application.security.BiometricAuthService
import de.christian2003.core.security.domain.services.HmacCipherService
import de.christian2003.passwordvault.application.security.ClipboardService
import de.christian2003.passwordvault.domain.model.target.PackageFingerprintService
import de.christian2003.passwordvault.plugin.infrastructure.db.PasswordVaultDatabase
import de.christian2003.passwordvault.plugin.infrastructure.db.PasswordVaultRepository
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.AccountDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.DetailDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.TagDao
import de.christian2003.passwordvault.plugin.infrastructure.db.dao.TargetDao
import de.christian2003.passwordvault.plugin.infrastructure.packages.AndroidPackageFingerprintService
import de.christian2003.passwordvault.plugin.infrastructure.packages.LocalPackagesRepository
import de.christian2003.passwordvault.plugin.infrastructure.security.AesHmacCipherService
import de.christian2003.passwordvault.plugin.infrastructure.security.AndroidClipboardService
import de.christian2003.passwordvault.plugin.infrastructure.security.auth.AndroidBiometricAuthService
import de.christian2003.passwordvault.plugin.infrastructure.security.auth.SharedPreferencesAuthRepository
import javax.inject.Singleton


/**
 * Hilt module for singleton-scoped bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: SharedPreferencesAuthRepository
    ): AuthRepository

    @Binds
    abstract fun bindPackagesRepository(
        impl: LocalPackagesRepository
    ): PackagesRepository

    @Binds
    abstract fun bindAccountRepository(
        impl: PasswordVaultRepository
    ): AccountRepository

    @Binds
    abstract fun bindTagRepository(
        impl: PasswordVaultRepository
    ): TagRepository

}


/**
 * Hilt module for activity-scoped bindings.
 */
@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @Binds
    abstract fun bindBiometricAuthService(
        impl: AndroidBiometricAuthService
    ): BiometricAuthService

}


/**
 * Hilt module for singleton-scoped providers.
 */
@Module
@InstallIn(SingletonComponent::class)
class AndroidSystemModule {

    @Provides
    @Singleton
    fun providePasswordVaultDatabase(
        @ApplicationContext context: Context
    ): PasswordVaultDatabase = PasswordVaultDatabase.getInstance(context)

    @Provides
    fun provideAccountDao(db: PasswordVaultDatabase): AccountDao = db.accountDao

    @Provides
    fun provideDetailDao(db: PasswordVaultDatabase): DetailDao = db.detailDao

    @Provides
    fun provideTagDao(db: PasswordVaultDatabase): TagDao = db.tagDao

    @Provides
    fun provideTargetDao(db: PasswordVaultDatabase): TargetDao = db.targetDao

    @Provides
    @Singleton
    fun providePackageManager(
        @ApplicationContext context: Context
    ): PackageManager = context.packageManager

    @Provides
    @Singleton
    fun provideClipboardManager(
        @ApplicationContext context: Context
    ): ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    @Provides
    fun provideCipherService(): HmacCipherService = AesHmacCipherService()

    @Provides
    fun provideClipboardService(clipboardManager: ClipboardManager): ClipboardService = AndroidClipboardService(clipboardManager)

    @Provides
    fun providePackageFingerprintService(
        packageManager: PackageManager
    ): PackageFingerprintService = AndroidPackageFingerprintService(packageManager)

}
