package de.christian2003.feature.autofill.application.usecases

import de.christian2003.feature.autofill.domain.entities.AutofillItem
import de.christian2003.feature.autofill.domain.entities.AutofillPartition
import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.domain.entities.FillRequestData
import de.christian2003.feature.autofill.domain.entities.FillResponseData
import javax.inject.Inject


internal class FillRequestUseCase @Inject constructor(

) {

    fun request(data: FillRequestData): FillResponseData {
        val autofillItems: MutableList<AutofillItem> = mutableListOf()

        data.requestedTypes.forEach { type ->
            if (type.partition == AutofillPartition.Credentials) {
                val item = AutofillItem(
                    label = when (type) {
                        AutofillType.Password -> "MyPassword"
                        AutofillType.Username -> "MyUsername"
                        AutofillType.EmailAddress -> "MyEmailAddress"
                        else -> "MyInvalid"
                    },
                    content = when (type) {
                        AutofillType.Password -> "MySecretPassword123"
                        AutofillType.Username -> "HelloWorld"
                        AutofillType.EmailAddress -> "abc@def.de"
                        else -> "-Invalid-"
                    },
                    type = type
                )
                autofillItems.add(item)
            }
        }

        return FillResponseData(
            items = autofillItems,
            partition = AutofillPartition.Credentials
        )
    }

}
