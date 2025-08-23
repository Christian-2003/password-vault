package de.christian2003.passwordvault.domain.entry


/**
 * Stores all types of details that are available.
 */
enum class DetailType(

    /**
     * Default icon to use for the detail type.
     */
    val defaultIcon: DetailIcon

) {

    TEXT(DetailIcon.TEXT),

    NUMBER(DetailIcon.NUMBER),

    SECURITY_QUESTION(DetailIcon.SECURITY_QUESTION),

    ADDRESS(DetailIcon.ADDRESS),

    DATE(DetailIcon.DATE),

    EMAIL(DetailIcon.EMAIL),

    PASSWORD(DetailIcon.PASSWORD),

    URL(DetailIcon.URL),

    PIN(DetailIcon.PIN)

}
