package de.christian2003.feature.autofill.application.usecases

import android.util.Log
import de.christian2003.data.accounts.application.usecases.GetAccountsByIdsUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.feature.autofill.domain.entities.AutofillItem
import de.christian2003.feature.autofill.domain.entities.AutofillResponse
import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.application.services.AutofillTypeMapper
import de.christian2003.feature.autofill.domain.AddressParserService
import javax.inject.Inject
import kotlin.uuid.Uuid


internal class FetchAutofillDataUseCase @Inject constructor(
    private val getAccountsByIdsUseCase: GetAccountsByIdsUseCase,
    private val addressParserService: AddressParserService,
    private val autofillTypeMapper: AutofillTypeMapper
) {

    private var addressPartsCache: Map<AutofillType, String>? = null


    suspend fun fetchData(accountIds: Set<Uuid>, autofillTypes: Set<AutofillType>): List<AutofillResponse> {
        val accounts: List<Account> = getAccountsByIdsUseCase.getAccountsByIds(accountIds.toList())
        val detailTypes: Map<AutofillType, DetailType> = autofillTypesToDetailTypes(autofillTypes)

        val autofillResponses: MutableList<AutofillResponse> = mutableListOf()

        Log.d("Autofill", "Fetched ${accounts.size} accounts")

        accounts.forEach { account ->
            val response: AutofillResponse = generateAutofillResponse(
                account = account,
                detailTypes = detailTypes
            )
            autofillResponses.add(response)
        }

        return autofillResponses
    }


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


    private suspend fun generateAutofillResponse(account: Account, detailTypes: Map<AutofillType, DetailType>): AutofillResponse {
        Log.d("Autofill", "Generate response for account ${account.descriptor.name}")
        val items: MutableList<AutofillItem> = mutableListOf()

        detailTypes.forEach { (autofillType, detailType) ->
            Log.d("Autofill", "DetailType=$detailType, autofillType=$autofillType (${account.details.size} details)")
            val detail: Detail? = account.details.find { it.type == detailType }
            if (detail != null) {
                if (isAddress(autofillType)) {
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
                else {
                    val item = AutofillItem(
                        label = detail.name,
                        content = detail.content,
                        type = autofillType,
                        isObfuscated = detail.metadata.isObfuscated
                    )
                    items.add(item)
                }
                Log.d("Autofill", "Add item for detail ${detail.name}")
            }
        }

        val response = AutofillResponse(
            accountId = account.descriptor.id,
            items = items
        )

        return response
    }


    private fun isAddress(type: AutofillType): Boolean {
        return type == AutofillType.PostalCode
                || type == AutofillType.PostalCodeExtended
                || type == AutofillType.PostalAddress
                || type == AutofillType.AddressAuxiliaryDetails
                || type == AutofillType.AddressCountry
                || type == AutofillType.AddressLocality
                || type == AutofillType.AddressRegion
                || type == AutofillType.AddressStreet
    }


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

}
