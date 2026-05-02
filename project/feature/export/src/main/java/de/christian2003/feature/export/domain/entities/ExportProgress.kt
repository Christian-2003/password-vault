package de.christian2003.feature.export.domain.entities


internal data class ExportProgress(
    val progress: Float,
    val state: ProgressState
)
