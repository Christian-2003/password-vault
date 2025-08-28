package de.christian2003.passwordvault.domain.entry

import kotlin.uuid.Uuid


/**
 * Models a tag that can be assigned to an entry for easier grouping.
 */
class Tag(

    /**
     * UUID of the tag.
     */
    val id: Uuid = Uuid.random(),

    /**
     * Name of the tag. This name is displayed to the user.
     */
    var name: String

) {

    /**
     * Returns the hash code for the tag. This is identical to the hash code of the ID of the tag.
     *
     * @return  Hash code for the tag.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }


    /**
     * Tests whether the object passed is equal to this tag, based on their IDs.
     *
     * @param other Other object to test.
     * @return      Whether the IDs of both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return (other is Tag) && (other.id == this.id)
    }

}
