package de.christian2003.feature.autofill.domain.entities


/**
 * Value object contains the result of the fill request
 */
internal data class FillResponseData(
    val items: List<AutofillItem>,
    val partition: AutofillPartition
) {

    /**
     * Initializes a new fill response.
     */
    init {
        items.forEach { item ->
            require(item.type.partition == partition) { "Response partition must match item type" }
        }
    }

}
