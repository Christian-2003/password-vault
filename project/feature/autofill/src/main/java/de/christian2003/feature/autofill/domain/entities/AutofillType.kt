package de.christian2003.feature.autofill.domain.entities


/**
 * Types of autofill data that can be retrieved by the app.
 *
 * @param partition Partition to which the data is assigned.
 * @param group     Group for the autofill type. Types of the same group can be derived from a single
 *                  account detail (usually).
 */
internal enum class AutofillType(
    val partition: AutofillPartition,
    val group: AutofillGroup
) {

    //Credentials:
    Username(AutofillPartition.Credentials, AutofillGroup.Other),
    Password(AutofillPartition.Credentials, AutofillGroup.Other),
    EmailAddress(AutofillPartition.Credentials, AutofillGroup.Other),

    //Address:
    PostalCode(AutofillPartition.Address, AutofillGroup.Address),
    PostalCodeExtended(AutofillPartition.Address, AutofillGroup.Address),
    PostalAddress(AutofillPartition.Address, AutofillGroup.Address),
    AddressAuxiliaryDetails(AutofillPartition.Address, AutofillGroup.Address),
    AddressCountry(AutofillPartition.Address, AutofillGroup.Address),
    AddressLocality(AutofillPartition.Address, AutofillGroup.Address),
    AddressRegion(AutofillPartition.Address, AutofillGroup.Address),
    AddressStreet(AutofillPartition.Address, AutofillGroup.Address),

    //Birthday:
    BirthDateFull(AutofillPartition.PersonalInfo, AutofillGroup.Birthday),
    BirthDateDay(AutofillPartition.PersonalInfo, AutofillGroup.Birthday),
    BirthDateMonth(AutofillPartition.PersonalInfo, AutofillGroup.Birthday),
    BirthDateYear(AutofillPartition.PersonalInfo, AutofillGroup.Birthday),

    //Credit card:
    CreditCardNumber(AutofillPartition.PaymentInfo, AutofillGroup.Other),
    CreditCardSecurityCode(AutofillPartition.PaymentInfo, AutofillGroup.Other),
    CreditCardExpirationDate(AutofillPartition.PaymentInfo, AutofillGroup.Other),
    CreditCardExpirationDay(AutofillPartition.PaymentInfo, AutofillGroup.Other),
    CreditCardExpirationMonth(AutofillPartition.PaymentInfo, AutofillGroup.Other),
    CreditCardExpirationYear(AutofillPartition.PaymentInfo, AutofillGroup.Other),

    //Personal name:
    PersonFullName(AutofillPartition.PersonalInfo, AutofillGroup.PersonName),
    PersonFirstName(AutofillPartition.PersonalInfo, AutofillGroup.PersonName),
    PersonLastName(AutofillPartition.PersonalInfo, AutofillGroup.PersonName),
    PersonMiddleName(AutofillPartition.PersonalInfo, AutofillGroup.PersonName),
    PersonMiddleInitial(AutofillPartition.PersonalInfo, AutofillGroup.PersonName),
    PersonNamePrefix(AutofillPartition.PersonalInfo, AutofillGroup.PersonName),
    PersonNameSuffix(AutofillPartition.PersonalInfo, AutofillGroup.PersonName),

    //Other personal info:
    Gender(AutofillPartition.PersonalInfo, AutofillGroup.Other),

    //Phone number:
    PhoneNumber(AutofillPartition.PersonalInfo, AutofillGroup.PhoneNumber),
    PhoneNumberDevice(AutofillPartition.PersonalInfo, AutofillGroup.PhoneNumber),
    PhoneNumberNational(AutofillPartition.PersonalInfo, AutofillGroup.PhoneNumber),
    PhoneCountryCode(AutofillPartition.PersonalInfo, AutofillGroup.PhoneNumber)

}
