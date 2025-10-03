package de.christian2003.passwordvault.domain.model.account

import kotlin.uuid.Uuid


/**
 * Domain value object contains all descriptor values for an account. These values provide a very
 * brief summary for an account (containing name, description and ID) and nothing else.
 *
 * @param name          Name of the account.
 * @param description   Optional description for the account.
 * @param id            UUID of the account.
 */
data class AccountDescriptor(
    val name: String,
    val description: String = "",
    val id: Uuid = Uuid.random()
) {

    /**
     * Initializes the account descriptor.
     */
    init {
        require(name.isNotBlank()) { "Account name cannot be blank" }
        require(description.isNotBlank() || description.isEmpty()) { "Description must either be empty or not blank" }
        require(id != Uuid.NIL) { "Account ID cannot be NIL" }
    }


    /**
     * Converts the account descriptor to a string-representation that can be used for debugging.
     *
     * @return  String-representation.
     */
    override fun toString(): String {
        return "[Id: $id] [Name: '$name'] [Description: '$description']"
    }

}
