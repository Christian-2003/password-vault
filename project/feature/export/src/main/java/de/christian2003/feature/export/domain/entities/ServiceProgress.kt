package de.christian2003.feature.export.domain.entities


/**
 * Progress reporting of an active export / import service.
 *
 * @param progress  Progress (between 0.0 and 1.0).
 * @param state     State of the active export / import.
 */
internal data class ServiceProgress(
    val progress: Float,
    val state: ProgressState
)
