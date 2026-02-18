package de.christian2003.feature.autofill.application.services

import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.feature.autofill.domain.entities.AutofillType
import javax.inject.Inject


/**
 * Mapper can map an autofill type to a detail type.
 */
internal class AutofillTypeMapper @Inject constructor() {

    /**
     * Maps the provided autofill type to a detail type. If an autofill type cannot be mapped, null
     * is returned.
     *
     * @param autofillType  Autofill type to map to a detail type.
     * @return              Mapped detail type or null.
     */
    fun toDetailType(autofillType: AutofillType): DetailType? {
        return when (autofillType) {
            //Credentials:
            AutofillType.Username -> DetailType.Username
            AutofillType.Password -> DetailType.Password
            AutofillType.EmailAddress -> DetailType.Email

            //Address:
            AutofillType.PostalCode -> DetailType.Address
            AutofillType.PostalCodeExtended -> DetailType.Address
            AutofillType.PostalAddress -> DetailType.Address
            AutofillType.AddressAuxiliaryDetails -> DetailType.Address
            AutofillType.AddressCountry -> DetailType.Address
            AutofillType.AddressLocality -> DetailType.Address
            AutofillType.AddressRegion -> DetailType.Address
            AutofillType.AddressStreet -> DetailType.Address

            //Birthday:
            AutofillType.BirthDateFull -> DetailType.Date
            AutofillType.BirthDateDay -> DetailType.Date
            AutofillType.BirthDateMonth -> DetailType.Date
            AutofillType.BirthDateYear -> DetailType.Date

            //Personal name:
            AutofillType.PersonFullName -> DetailType.PersonalName
            AutofillType.PersonFirstName -> DetailType.PersonalName
            AutofillType.PersonLastName -> DetailType.PersonalName
            AutofillType.PersonMiddleName -> DetailType.PersonalName
            AutofillType.PersonMiddleInitial -> DetailType.PersonalName
            AutofillType.PersonNamePrefix -> DetailType.PersonalName
            AutofillType.PersonNameSuffix -> DetailType.PersonalName

            //Phone number:
            AutofillType.PhoneNumber -> DetailType.PhoneNumber
            AutofillType.PhoneNumberDevice -> DetailType.PhoneNumber
            AutofillType.PhoneNumberNational -> DetailType.PhoneNumber
            AutofillType.PhoneCountryCode -> DetailType.PhoneNumber

            //Other:
            AutofillType.CreditCardNumber -> DetailType.CreditCardNumber
            AutofillType.CreditCardSecurityCode -> DetailType.Pin

            else -> null
        }
    }

}
