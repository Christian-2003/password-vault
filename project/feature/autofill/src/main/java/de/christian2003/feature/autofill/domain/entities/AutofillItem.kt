package de.christian2003.feature.autofill.domain.entities


/**
 * Value object contains the data for a single remote view that can be auto-filled.
 *
 * @param label         Label for the item (e.g. the name of the detail).
 * @param content       Content for the item (e.g. a username, email or password).
 * @param type          Type.
 * @param isObfuscated  Whether the item should be obfuscated in the UI.
 */
internal data class AutofillItem(
    val label: String,
    val content: String,
    val type: AutofillType,
    val isObfuscated: Boolean
) {

    /**
     * Initializes a new autofill item.
     */
    init {
        require(label.isNotEmpty()) { "Label cannot be empty" }
        require(content.isNotEmpty()) { "Content cannot be empty" }
    }

}
