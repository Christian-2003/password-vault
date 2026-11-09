package de.christian2003.feature.export.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import de.christian2003.feature.export.domain.services.ServiceLauncherService
import de.christian2003.feature.export.domain.services.ExportService
import de.christian2003.feature.export.domain.services.ImportService
import de.christian2003.feature.export.infrastructure.backup.BackupService
import de.christian2003.feature.export.infrastructure.work.WorkerServiceLauncherService


@Module
@InstallIn(SingletonComponent::class)
internal abstract class ExportBindingsSingletonModule {

    //========================= DOMAIN SERVICES =========================

    @Binds
    @IntoSet
    abstract fun bindBackupExportService(
        impl: BackupService
    ): ExportService

    @Binds
    @IntoSet
    abstract fun bindBackupImportService(
        impl: BackupService
    ): ImportService

    @Binds
    abstract fun bindExportLauncherService(
        impl: WorkerServiceLauncherService
    ): ServiceLauncherService

}
