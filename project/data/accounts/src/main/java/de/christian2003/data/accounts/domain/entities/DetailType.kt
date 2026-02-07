package de.christian2003.data.accounts.domain.entities


/**
 * Stores all types of details that are available.
 *
 * @param defaultIcon   Default icon to use for the detail type.
 */
enum class DetailType(
    val defaultIcon: DetailIcon
) {

    Text(DetailIcon.Text),
    Number(DetailIcon.Number),
    SecurityQuestion(DetailIcon.SecurityQuestion),
    Address(DetailIcon.Address),
    Date(DetailIcon.Date),
    Email(DetailIcon.Email),
    Password(DetailIcon.Password),
    Url(DetailIcon.Url),
    Pin(DetailIcon.Pin)

}
