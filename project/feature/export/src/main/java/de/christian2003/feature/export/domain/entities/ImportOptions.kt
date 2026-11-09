package de.christian2003.feature.export.domain.entities


/**
 * Options describe what should happen with the existing / new data during an import.
 *
 * @property DeleteAllExistingData  If this is selected, all existing data is wiped. Afterwards, the
 *                                  imported data is restored.
 * @property OverrideDuplicates     If this is selected, duplicate items are overridden. Example:
 *                                  An account with ID 12345 already exists. It will be replaced if
 *                                  an account with the same ID is imported.
 * @property SkipDuplicates         If this is selected, duplicate items are skipped. Example:
 *                                  An account with ID 67890 already exists. If an account with the
 *                                  same ID would NOT be restored and the existing account is kept.
 */
internal enum class ImportOptions {

    DeleteAllExistingData,
    OverrideDuplicates,
    SkipDuplicates

}
