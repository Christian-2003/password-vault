package de.christian2003.feature.autofill.application.usecases

import de.christian2003.data.accounts.application.usecases.GetAccountsByIdsUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.feature.autofill.domain.entities.AutofillItem
import de.christian2003.feature.autofill.domain.entities.AutofillResponse
import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.application.services.AutofillTypeMapper
import de.christian2003.feature.autofill.domain.entities.AutofillGroup
import de.christian2003.feature.autofill.domain.repositories.AutofillConfigRepository
import de.christian2003.feature.autofill.domain.services.AddressParserService
import de.christian2003.feature.autofill.domain.services.DateParserService
import de.christian2003.feature.autofill.domain.services.PersonNameParserService
import de.christian2003.feature.autofill.domain.services.PhoneNumberParserService
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * Use case to fetch autofill data from the app and parse it into suitable parts if required.
 *
 * @param getAccountsByIdsUseCase   Use case to get a list of accounts based on their IDs.
 * @param addressParserService      Service to parse addresses.
 * @param personNameParserService   Service to parse person names.
 * @param phoneNumberParserService  Service to parse mobile phone numbers.
 * @param dateParserService         Service to parse dates.
 * @param autofillTypeMapper        Mapper maps autofill types to detail types.
 */
internal class FetchAutofillDataUseCase @Inject constructor(
    private val getAccountsByIdsUseCase: GetAccountsByIdsUseCase,
    private val addressParserService: AddressParserService,
    private val personNameParserService: PersonNameParserService,
    private val phoneNumberParserService: PhoneNumberParserService,
    private val dateParserService: DateParserService,
    private val autofillTypeMapper: AutofillTypeMapper
) {

    /**
     * Caches the parsed address.
     */
    private var addressPartsCache: Map<AutofillType, String>? = null

    /**
     * Caches the parsed person name.
     */
    private var personNamePartsCache: Map<AutofillType, String>? = null

    /**
     * Caches the parsed phone number.
     */
    private var phoneNumberPartsCache: Map<AutofillType, String>? = null

    /**
     * Caches the parsed date.
     */
    private var datePartsCache: Map<AutofillType, String>? = null


    /**
     * Fetches the autofill data.
     *
     * @param accountIds    IDs of the account whose data to fetch for the response.
     * @param autofillTypes Autofill types for which to fetch data.
     * @return              List of responses. One item for each account.
     */
    suspend fun fetchData(accountIds: Set<Uuid>, autofillTypes: Set<AutofillType>): List<AutofillResponse> {
        val accounts: List<Account> = getAccountsByIdsUseCase.getAccountsByIds(accountIds.toList())
        val detailTypes: Map<AutofillType, DetailType> = autofillTypesToDetailTypes(autofillTypes)

        val autofillResponses: MutableList<AutofillResponse> = mutableListOf()

        accounts.forEach { account ->
            val response: AutofillResponse = generateAutofillResponse(
                account = account,
                detailTypes = detailTypes
            )
            autofillResponses.add(response)
        }

        return autofillResponses
    }


    /**
     * Converts the specified set of autofill types to detail types. The resulting detail types are
     * mapped to the corresponding autofill type.
     *
     * @param autofillTypes Set of autofill types.
     * @return              Detail types mapped to their autofill type.
     */
    private fun autofillTypesToDetailTypes(autofillTypes: Set<AutofillType>): Map<AutofillType, DetailType> {
        val detailTypes: MutableMap<AutofillType, DetailType> = mutableMapOf()

        autofillTypes.forEach { autofillType ->
            val detailType: DetailType? = autofillTypeMapper.toDetailType(autofillType)
            if (detailType != null && !detailTypes.containsKey(autofillType)) {
                detailTypes[autofillType] = detailType
            }
        }

        return detailTypes
    }


    /**
     * Generates the autofill response for the provided account and detail types.
     *
     * @param account       Account containing the data with which to populate the response.
     * @param detailTypes   Detail types mapped to autofill types.
     * @return              Response containing the autofill data.
     */
    private suspend fun generateAutofillResponse(account: Account, detailTypes: Map<AutofillType, DetailType>): AutofillResponse {
        val items: MutableList<AutofillItem> = mutableListOf()

        detailTypes.forEach { (autofillType, detailType) ->
            val detail: Detail? = account.details.find { it.type == detailType }
            if (detail != null) {
                when(autofillType.group) {
                    AutofillGroup.Address -> {
                        val addressPart: String? = getAddressPartForAutofillType(detail.content, autofillType)
                        if (addressPart != null) {
                            val item = AutofillItem(
                                label = detail.name,
                                content = addressPart,
                                type = autofillType,
                                isObfuscated = detail.metadata.isObfuscated
                            )
                            items.add(item)
                        }
                    }
                    AutofillGroup.PersonName -> {
                        val namePart: String? = getPersonNamePartForAutofillType(detail.content, autofillType)
                        if (namePart != null) {
                            val item = AutofillItem(
                                label = detail.name,
                                content = namePart,
                                type = autofillType,
                                isObfuscated = detail.metadata.isObfuscated
                            )
                            items.add(item)
                        }
                    }
                    AutofillGroup.PhoneNumber -> {
                        val phonePart: String? = getPhoneNumberPartForAutofillType(detail.content, autofillType)
                        if (phonePart != null) {
                            val item = AutofillItem(
                                label = detail.name,
                                content = phonePart,
                                type = autofillType,
                                isObfuscated = detail.metadata.isObfuscated
                            )
                            items.add(item)
                        }
                    }
                    AutofillGroup.Birthday -> {
                        val datePart: String? = getDatePartForAutofillType(detail.content, autofillType)
                        if (datePart != null) {
                            val item = AutofillItem(
                                label = detail.name,
                                content = datePart,
                                type = autofillType,
                                isObfuscated = detail.metadata.isObfuscated
                            )
                            items.add(item)
                        }
                    }
                    AutofillGroup.Other -> {
                        val item = AutofillItem(
                            label = detail.name,
                            content = detail.content,
                            type = autofillType,
                            isObfuscated = detail.metadata.isObfuscated
                        )
                        items.add(item)
                    }
                }
            }
        }

        val response = AutofillResponse(
            accountId = account.descriptor.id,
            items = items
        )

        return response
    }


    /**
     * Returns the address part matching the specified autofill type or null if no data can be
     * returned.
     *
     * @param fullAddress   Full address from which to get the part.
     * @param type          Type for the result.
     * @return              Address part or null.
     */
    private suspend fun getAddressPartForAutofillType(fullAddress: String, type: AutofillType): String? {
        var addressPartsCache: Map<AutofillType, String> ? = this.addressPartsCache
        if (addressPartsCache == null) {
            addressPartsCache = addressParserService.parseAddressToParts(fullAddress)
            this.addressPartsCache = addressPartsCache
        }

        return if (addressPartsCache.containsKey(type)) {
            addressPartsCache[type]
        } else {
            null
        }
    }


    /**
     * Returns the person name part matching the specified autofill type or null if no data can be
     * returned.
     *
     * @param fullName  Full person name from which to get the part.
     * @param type      Type for the result.
     * @return          Person name part or null.
     */
    private suspend fun getPersonNamePartForAutofillType(fullName: String, type: AutofillType): String? {
        var personNamePartsCache: Map<AutofillType, String> ? = this.personNamePartsCache
        if (personNamePartsCache == null) {
            personNamePartsCache = personNameParserService.parseNameToParts(fullName)
            this.personNamePartsCache = personNamePartsCache
        }

        return if (personNamePartsCache.containsKey(type)) {
            personNamePartsCache[type]
        } else {
            null
        }
    }


    /**
     * Returns the phone number part matching the specified autofill type or null if no data can be
     * returned.
     *
     * @param fullNumber    Full phone number from which to get the part.
     * @param type          Type for the result.
     * @return              Phone number part or null.
     */
    private suspend fun getPhoneNumberPartForAutofillType(fullNumber: String, type: AutofillType): String? {
        var phoneNumberPartsCache: Map<AutofillType, String> ? = this.phoneNumberPartsCache
        if (phoneNumberPartsCache == null) {
            phoneNumberPartsCache = phoneNumberParserService.parsePhoneNumberToParts(fullNumber)
            this.phoneNumberPartsCache = phoneNumberPartsCache
        }

        return if (phoneNumberPartsCache.containsKey(type)) {
            phoneNumberPartsCache[type]
        } else {
            null
        }
    }


    /**
     * Returns the date part matching the specified autofill type or null if no data can be returned.
     *
     * @param fullDate  Full date from which to get the part.
     * @param type      Type for the result.
     * @return          Date part or null.
     */
    private suspend fun getDatePartForAutofillType(fullDate: String, type: AutofillType): String? {
        var datePartsCache: Map<AutofillType, String> ? = this.datePartsCache
        if (datePartsCache == null) {
            datePartsCache = dateParserService.parseDateToParts(fullDate)
            this.datePartsCache = datePartsCache
        }

        return if (datePartsCache.containsKey(type)) {
            datePartsCache[type]
        } else {
            null
        }
    }

}
