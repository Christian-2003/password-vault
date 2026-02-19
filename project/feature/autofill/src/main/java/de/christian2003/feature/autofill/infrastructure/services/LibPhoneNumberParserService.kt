package de.christian2003.feature.autofill.infrastructure.services

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.domain.services.PhoneNumberParserService
import javax.inject.Inject


/**
 * Implementation for the service to parse a phone number into individual parts. The service uses
 * the Googlecode Libphonenumber library for parsing..
 */
internal class LibPhoneNumberParserService @Inject constructor(): PhoneNumberParserService {

    /**
     * Phone number utils.
     */
    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()


    /**
     * Parses the specified full phone number into individual parts.
     *
     * @param fullNumber    Full phone number to parse.
     * @return              Individual parts of the phone number parsed into individual parts that
     *                      are mapped to their corresponding autofill type.
     */
    override suspend fun parsePhoneNumberToParts(fullNumber: String): Map<AutofillType, String> {
        if (fullNumber.isBlank()) {
            return emptyMap()
        }

        return try {
            val parsed: Phonenumber.PhoneNumber = phoneUtil.parse(fullNumber, null)

            if (!phoneUtil.isValidNumber(parsed)) {
                return emptyMap()
            }

            val result: MutableMap<AutofillType, String> = mutableMapOf()

            // Full E.164
            result[AutofillType.PhoneNumber] = phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)

            // Country Code
            result[AutofillType.PhoneCountryCode] = "+${parsed.countryCode}"

            // National number
            result[AutofillType.PhoneNumberNational] = phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)

            // Device-friendly format
            result[AutofillType.PhoneNumberDevice] = phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)

            result

        } catch (_: Exception) {
            emptyMap()
        }
    }

}
