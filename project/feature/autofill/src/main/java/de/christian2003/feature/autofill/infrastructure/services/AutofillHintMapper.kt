package de.christian2003.feature.autofill.infrastructure.services

import de.christian2003.feature.autofill.domain.entities.AutofillType
import javax.inject.Inject
import androidx.autofill.HintConstants


/**
 * Mapper maps the official android-specific autofill hints to their domain representation.
 */
internal class AutofillHintMapper @Inject constructor() {

    /**
     * Returns the domain representation of the specified autofill hint. If no domain type can be
     * determined, null is returned.
     *
     * @param hint  Android autofill hint to map to the corresponding domain type.
     * @return      Domain type for the specified autofill hint or null.
     */
    fun toDomain(hint: String): AutofillType? {
        return when (hint) {
            //Credentials:
            HintConstants.AUTOFILL_HINT_USERNAME -> AutofillType.Username
            HintConstants.AUTOFILL_HINT_NEW_USERNAME -> AutofillType.Username
            HintConstants.AUTOFILL_HINT_PASSWORD -> AutofillType.Password
            HintConstants.AUTOFILL_HINT_NEW_PASSWORD -> AutofillType.Password
            HintConstants.AUTOFILL_HINT_WIFI_PASSWORD-> AutofillType.Password
            HintConstants.AUTOFILL_HINT_EMAIL_ADDRESS -> AutofillType.EmailAddress

            //Address:
            HintConstants.AUTOFILL_HINT_POSTAL_CODE -> AutofillType.PostalCode
            HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_EXTENDED_POSTAL_CODE -> AutofillType.PostalCodeExtended
            HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS -> AutofillType.PostalAddress
            HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_EXTENDED_ADDRESS -> AutofillType.AddressAuxiliaryDetails
            HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_COUNTRY -> AutofillType.AddressCountry
            HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_LOCALITY -> AutofillType.AddressLocality
            HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_DEPENDENT_LOCALITY -> AutofillType.AddressLocality
            HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_REGION -> AutofillType.AddressRegion
            HintConstants.AUTOFILL_HINT_POSTAL_ADDRESS_STREET_ADDRESS -> AutofillType.AddressStreet

            //Birthday:
            HintConstants.AUTOFILL_HINT_BIRTH_DATE_FULL -> AutofillType.BirthDateFull
            HintConstants.AUTOFILL_HINT_BIRTH_DATE_DAY -> AutofillType.BirthDateDay
            HintConstants.AUTOFILL_HINT_BIRTH_DATE_MONTH -> AutofillType.BirthDateMonth
            HintConstants.AUTOFILL_HINT_BIRTH_DATE_YEAR -> AutofillType.BirthDateYear

            //Credit card:
            HintConstants.AUTOFILL_HINT_CREDIT_CARD_NUMBER -> AutofillType.CreditCardNumber
            HintConstants.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE -> AutofillType.CreditCardSecurityCode
            HintConstants.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE -> AutofillType.CreditCardExpirationDate
            HintConstants.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DAY -> AutofillType.CreditCardExpirationDay
            HintConstants.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_MONTH -> AutofillType.CreditCardExpirationMonth
            HintConstants.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_YEAR -> AutofillType.CreditCardExpirationYear

            //Personal name:
            HintConstants.AUTOFILL_HINT_PERSON_NAME -> AutofillType.PersonFullName
            HintConstants.AUTOFILL_HINT_NAME -> AutofillType.PersonFullName
            HintConstants.AUTOFILL_HINT_PERSON_NAME_GIVEN -> AutofillType.PersonFirstName
            HintConstants.AUTOFILL_HINT_PERSON_NAME_FAMILY -> AutofillType.PersonLastName
            HintConstants.AUTOFILL_HINT_PERSON_NAME_MIDDLE -> AutofillType.PersonMiddleName
            HintConstants.AUTOFILL_HINT_PERSON_NAME_MIDDLE_INITIAL -> AutofillType.PersonMiddleInitial
            HintConstants.AUTOFILL_HINT_PERSON_NAME_PREFIX -> AutofillType.PersonNamePrefix
            HintConstants.AUTOFILL_HINT_PERSON_NAME_SUFFIX -> AutofillType.PersonNameSuffix

            //Other personal info:
            HintConstants.AUTOFILL_HINT_GENDER -> AutofillType.Gender

            //Phone number:
            HintConstants.AUTOFILL_HINT_PHONE_NUMBER -> AutofillType.PhoneNumber
            HintConstants.AUTOFILL_HINT_PHONE -> AutofillType.PhoneNumber
            HintConstants.AUTOFILL_HINT_PHONE_NUMBER_DEVICE -> AutofillType.PhoneNumberDevice
            HintConstants.AUTOFILL_HINT_PHONE_NATIONAL -> AutofillType.PhoneNumberNational
            HintConstants.AUTOFILL_HINT_PHONE_COUNTRY_CODE -> AutofillType.PhoneCountryCode

            //Unknown
            else -> null
        }
    }

}
