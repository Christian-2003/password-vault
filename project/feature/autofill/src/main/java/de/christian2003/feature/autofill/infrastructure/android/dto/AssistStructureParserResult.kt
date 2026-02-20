package de.christian2003.feature.autofill.infrastructure.android.dto

import android.view.autofill.AutofillId
import de.christian2003.feature.autofill.domain.entities.AutofillPartition
import de.christian2003.feature.autofill.domain.entities.AutofillType


/**
 * Result returned by the AssistStructureParser once an assist structure has been parsed successfully.
 *
 * @param data                      List of autofill IDs mapped to their corresponding type.
 * @param focusedAutofillId         Autofill ID of the focused view.
 * @param focusedAutofillPartition  Partition of the focused view.
 */
internal data class AssistStructureParserResult(
    val data: Map<AutofillType, List<AutofillId>>,
    val focusedAutofillId: AutofillId,
    val focusedAutofillPartition: AutofillPartition
)
