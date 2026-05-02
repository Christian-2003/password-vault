package de.christian2003.feature.export.domain.entities


/**
 * Progress states for an export or import.
 *
 * @property Enqueued   The process has not started yet but is enqueued and scheduled.
 * @property Running    The process is currently running.
 * @property Finished   The process has finished successfully.
 * @property Failed     The process has aborted due to a failure.
 */
internal enum class ProgressState {

    Enqueued,
    Running,
    Finished,
    Failed

}
