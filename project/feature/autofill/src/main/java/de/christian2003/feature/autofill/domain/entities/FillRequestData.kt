package de.christian2003.feature.autofill.domain.entities


/**
 * Value object contains the data for an autofill request.
 *
 * @param packageName       Package name to autofill.
 * @param requestedTypes    List of types to autofill.
 */
internal data class FillRequestData(
    val packageName: String,
    val requestedTypes: List<AutofillType>
) {

    /**
     * Initializes a new fill request.
     */
    init {
        require(packageName.isNotBlank()) { "Package name cannot be blank" }
    }

}
