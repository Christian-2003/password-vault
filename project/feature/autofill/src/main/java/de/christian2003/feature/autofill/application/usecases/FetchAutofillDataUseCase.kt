package de.christian2003.feature.autofill.application.usecases

import android.util.Log
import de.christian2003.data.accounts.application.usecases.GetAccountsByIdsUseCase
import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.feature.autofill.domain.entities.AutofillItem
import de.christian2003.feature.autofill.domain.entities.AutofillResponse
import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.infrastructure.mapper.AutofillTypeMapper
import javax.inject.Inject
import kotlin.uuid.Uuid


internal class FetchAutofillDataUseCase @Inject constructor(
    private val getAccountsByIdsUseCase: GetAccountsByIdsUseCase,
    private val autofillTypeMapper: AutofillTypeMapper
) {

    suspend fun fetchData(accountIds: Set<Uuid>, autofillTypes: Set<AutofillType>): List<AutofillResponse> {
        val accounts: List<Account> = getAccountsByIdsUseCase.getAccountsByIds(accountIds.toList())
        val detailTypes: Map<DetailType, AutofillType> = autofillTypesToDetailTypes(autofillTypes)

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


    private fun autofillTypesToDetailTypes(autofillTypes: Set<AutofillType>): Map<DetailType, AutofillType> {
        val detailTypes: MutableMap<DetailType, AutofillType> = mutableMapOf()

        autofillTypes.forEach { autofillType ->
            val detailType: DetailType? = autofillTypeMapper.toDetailType(autofillType)
            if (detailType != null && !detailTypes.containsKey(detailType)) {
                detailTypes[detailType] = autofillType
            }
        }

        return detailTypes
    }


    private fun generateAutofillResponse(account: Account, detailTypes: Map<DetailType, AutofillType>): AutofillResponse {
        Log.d("Autofill", "Generate response for account ${account.descriptor.name}")
        val items: MutableList<AutofillItem> = mutableListOf()

        detailTypes.forEach { (detailType, autofillType) ->
            Log.d("Autofill", "DetailType=$detailType, autofillType=$autofillType (${account.details.size} details)")
            val detail: Detail? = account.details.find { it.type == detailType }
            if (detail != null) {
                val item = AutofillItem(
                    label = detail.name,
                    content = detail.content,
                    type = autofillType,
                    isObfuscated = detail.metadata.isObfuscated
                )
                items.add(item)
                Log.d("Autofill", "Add item for detail ${detail.name}")
            }
        }

        val response = AutofillResponse(
            accountId = account.descriptor.id,
            items = items
        )

        return response
    }

}
