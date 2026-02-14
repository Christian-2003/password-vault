package de.christian2003.feature.autofill.domain.entities


/**
 * Types of autofill data that can be retrieved by the app.
 *
 * @param partition Partition to which the data is assigned.
 */
internal enum class AutofillType(
    val partition: AutofillPartition
) {

    //Credentials:
    Username(AutofillPartition.Credentials),
    Password(AutofillPartition.Credentials),
    EmailAddress(AutofillPartition.Credentials),

    //Address:
    PostalCode(AutofillPartition.Address),
    PostalCodeExtended(AutofillPartition.Address),
    PostalAddress(AutofillPartition.Address),
    AddressAuxiliaryDetails(AutofillPartition.Address),
    AddressCountry(AutofillPartition.Address),
    AddressLocality(AutofillPartition.Address),
    AddressRegion(AutofillPartition.Address),
    AddressStreet(AutofillPartition.Address),

    //Birthday:
    BirthDateFull(AutofillPartition.PersonalInfo),
    BirthDateDay(AutofillPartition.PersonalInfo),
    BirthDateMonth(AutofillPartition.PersonalInfo),
    BirthDateYear(AutofillPartition.PersonalInfo),

    //Credit card:
    CreditCardNumber(AutofillPartition.PaymentInfo),
    CreditCardSecurityCode(AutofillPartition.PaymentInfo),
    CreditCardExpirationDate(AutofillPartition.PaymentInfo),
    CreditCardExpirationDay(AutofillPartition.PaymentInfo),
    CreditCardExpirationMonth(AutofillPartition.PaymentInfo),
    CreditCardExpirationYear(AutofillPartition.PaymentInfo),

    //Personal name:
    PersonFullName(AutofillPartition.PersonalInfo),
    PersonFirstName(AutofillPartition.PersonalInfo),
    PersonLastName(AutofillPartition.PersonalInfo),
    PersonMiddleName(AutofillPartition.PersonalInfo),
    PersonMiddleInitial(AutofillPartition.PersonalInfo),
    PersonNamePrefix(AutofillPartition.PersonalInfo),
    PersonNameSuffix(AutofillPartition.PersonalInfo),

    //Other personal info:
    Gender(AutofillPartition.PersonalInfo),

    //Phone number:
    PhoneNumber(AutofillPartition.PersonalInfo),
    PhoneNumberDevice(AutofillPartition.PersonalInfo),
    PhoneNumberNational(AutofillPartition.PersonalInfo),
    PhoneCountryCode(AutofillPartition.PersonalInfo)

}
