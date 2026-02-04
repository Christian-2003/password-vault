package de.christian2003.data.accounts.domain.entities


/**
 * Stores all types of details that are available.
 *
 * @param defaultIcon   Default icon to use for the detail type.
 */
enum class DetailType(
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
