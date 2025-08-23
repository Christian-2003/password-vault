package de.christian2003.passwordvault.domain.entry

import java.time.LocalDateTime
import kotlin.uuid.Uuid


/**
 * Domain entity models an entry (i.e. an account).
 */
class Entry (

    /**
     * UUID for the entry.
     */
    var id: Uuid = Uuid.random(),

    /**
     * Name for the entry, which is set by the user.
     */
    var name: String = "",

    /**
     * Description for the entry, which is set by the user.
     */
    var description: String = "",

    /**
     * Date time on which the entry was created. This is for statistical purposes.
     */
    val created: LocalDateTime = LocalDateTime.now(),

    /**
     * Date time on which the entry was edited the last time. This is for statistical purposes.
     */
    var edited: LocalDateTime = LocalDateTime.now()

) {

    /**
     * Returns the hash code for the entry. This is identical to the hash code of the ID of the
     * entry.
     *
     * @return  Hash code for the entry.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }


    /**
     * Tests whether the object passed is equal to this entry, based on their IDs.
     *
     * @param other Other object to test.
     * @return      Whether the IDs of both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return other is Entry && other.id == this.id
    }

}
