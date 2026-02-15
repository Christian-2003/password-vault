package de.christian2003.feature.autofill.domain.entities

import kotlin.uuid.Uuid


internal data class AutofillResponse(
    val accountId: Uuid,
    val items: List<AutofillItem>
)
