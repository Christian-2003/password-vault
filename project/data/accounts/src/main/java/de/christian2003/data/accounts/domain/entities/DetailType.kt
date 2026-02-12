package de.christian2003.data.accounts.domain.entities


/**
 * Stores all types of details that are available.
 *
 * @param defaultIcon   Default icon to use for the detail type.
 */
enum class DetailType(
    val defaultIcon: DetailIcon
) {

    Email(DetailIcon.Email),
    Username(DetailIcon.Username),
    Password(DetailIcon.Password),
    Pin(DetailIcon.Pin),
    SecurityQuestion(DetailIcon.SecurityQuestion),
    PersonalName(DetailIcon.Identification),
    PhoneNumber(DetailIcon.Phone),
    Date(DetailIcon.Date),
    Url(DetailIcon.Url),
    Address(DetailIcon.Address),
    CreditCardNumber(DetailIcon.Payment),
    Text(DetailIcon.Text),
    Number(DetailIcon.Number)

}
