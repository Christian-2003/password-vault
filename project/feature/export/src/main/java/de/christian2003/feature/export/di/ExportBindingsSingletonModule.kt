package de.christian2003.feature.export.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import de.christian2003.feature.export.domain.services.ExportLauncherService
import de.christian2003.feature.export.domain.services.ExportService
import de.christian2003.feature.export.infrastructure.backup.BackupService
import de.christian2003.feature.export.infrastructure.work.WorkerExportLauncherService


@Module
@InstallIn(SingletonComponent::class)
internal abstract class ExportBindingsSingletonModule {

    //========================= DOMAIN SERVICES =========================

    @Binds
    @IntoSet
    abstract fun bindBackupService(
        impl: BackupService
    ): ExportService

    @Binds
    abstract fun bindExportLauncherService(
        impl: WorkerExportLauncherService
    ): ExportLauncherService

}
