package de.christian2003.data.accounts.domain.entities

import java.time.LocalDateTime


/**
 * Domain entity models an account (i.e. an account).
 *
 * @param descriptor    Descriptor contains base information about the account, such as the name,
 *                      description and ID.
 * @param details       List of details associated with the account.
 * @param tags          List of tags associated with the account.
 * @param metadata      Metadata for the account.
 */
class Account (
    descriptor: AccountDescriptor,
    details: List<Detail> = listOf(),
    tags: List<Tag> = listOf(),
    metadata: AccountMetadata = AccountMetadata()
) {

    /**
     * Descriptor contains base information about the account, such as the name, description and ID.
     */
    var descriptor: AccountDescriptor = descriptor
        set(value) {
            if (field != value) {
                field = value
                metadata = metadata.copy(editedAt = LocalDateTime.now())
            }
        }

    /**
     * List of details associated with the account.
     */
    var details: List<Detail> = details
        get() {
            return field.toList()
        }
        set(value) {
            field = value.toList()
            metadata = metadata.copy(editedAt = LocalDateTime.now())
        }

    /**
     * List of tags that are associated with the account.
     */
    var tags: List<Tag> = tags
        get() {
            return field.toList()
        }
        set(value) {
            if (field != value) {
                field = value.toList()
                metadata = metadata.copy(editedAt = LocalDateTime.now())
            }
        }

    /**
     * List of targets for the account.
     */
    var targets: List<Target>
        get() {
            return descriptor.targets.toList()
        }
        set(value) {
            if (descriptor.targets != value) {
                descriptor = descriptor.copy(targets = value)
                metadata = metadata.copy(editedAt = LocalDateTime.now())
            }
        }

    /**
     * Metadata for the account.
     */
    var metadata: AccountMetadata = metadata
        private set


    /**
     * Initializes the account instance.
     */
    init {
        this.descriptor = descriptor
        this.details = details
        this.tags = tags
        this.metadata = metadata
    }


    /**
     * Returns the hash code for the account. This is identical to the hash code of the ID of the
     * account.
     *
     * @return  Hash code for the account.
     */
    override fun hashCode(): Int {
        return descriptor.id.hashCode()
    }


    /**
     * Tests whether the object passed is equal to this account, based on their IDs.
     *
     * @param other Other object to test.
     * @return      Whether the IDs of both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return (other is Account) && (other.descriptor.id == this.descriptor.id)
    }


    /**
     * Converts this account instance into a string format. This string format can be used for
     * debugging and logging.
     *
     * @return  String representation of this account instance.
     */
    override fun toString(): String {
        return "[Descriptor: $descriptor] [Metadata: $metadata]"
    }

}
