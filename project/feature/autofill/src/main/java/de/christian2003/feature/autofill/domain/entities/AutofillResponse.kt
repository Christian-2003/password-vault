package de.christian2003.feature.autofill.domain.entities

import kotlin.uuid.Uuid


/**
 * Response for the FetchAutofillDataUseCase.
 *
 * @param accountId ID of the account whose data is returned.
 * @param items     List of autofill items containing the actual data.
 */
internal data class AutofillResponse(
    val accountId: Uuid,
    val items: List<AutofillItem>
)
