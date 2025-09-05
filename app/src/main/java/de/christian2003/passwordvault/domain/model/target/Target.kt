package de.christian2003.passwordvault.domain.model.target

import kotlin.uuid.Uuid

/**
 * Target for the autofill system. Each entry can have multiple targets, for which the app should
 * autofill login data. A target describes which app or website should receive autofill information.
 */
data class Target(

    /**
     * ID for the target.
     */
    val id: Uuid,

    /**
     * ID of the entry, for which to create the target.
     */
    val entry: Uuid,

    /**
     * Name of the target. For Android apps, this is the package name (e.g. "de.christian2003.passwordvault").
     * For websites, this is the domain name (e.g. "passwordvault.christian2003.de").
     */
    val name: String,

    /**
     * URL for the target.
     * For Android apps, this is of the following format: "android://<certificate hash>@<package name>"
     * (example: "android://Y8IbhARlHA0OYfIi72WsJSkEeiYPlWiu-agbEF2_B0tzeO6rIq1wHRtlLcS-pfoPgOlfZitB9x0NeNbO88dBLQ==@de.christian2003.passwordvault/").
     * For websites, this is the URL which to autofill. (e.g. "https://passwordvault.chrisitan2003.de/login").
     */
    val url: String,

    /**
     * If the target is a website, this is the name of the file in which the website favicon is
     * stored in the app. Favicons are stored in the internal app storage in "favicons/<name>.png".
     * If the target is an Android app, this is null.
     */
    val faviconFile: String?

) {

    /**
     * Returns the hash code for the target. This is identical to the hash code of the ID of the target.
     *
     * @return  Hash code for the target.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }


    /**
     * Tests whether the object passed is equal to this target, based on their IDs.
     *
     * @param other Other object to test.
     * @return      Whether the IDs of both objects are identical.
     */
    override fun equals(other: Any?): Boolean {
        return (other is Target) && (other.id == this.id)
    }

}