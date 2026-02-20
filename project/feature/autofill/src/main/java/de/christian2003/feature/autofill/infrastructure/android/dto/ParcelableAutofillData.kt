package de.christian2003.feature.autofill.infrastructure.android.dto

import android.os.Parcel
import android.os.Parcelable
import android.view.autofill.AutofillId
import de.christian2003.data.accounts.domain.entities.AccountCapability
import de.christian2003.feature.autofill.domain.entities.AutofillPartition
import de.christian2003.feature.autofill.domain.entities.AutofillType
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


internal class ParcelableAutofillData(
    val fieldMap: Map<AutofillType, List<AutofillId>>,
    val focusedAutofillId: AutofillId,
    val focusedAutofillPartition: AutofillPartition,
    val capabilities: List<AccountCapability>
): Parcelable {

    constructor(parcel: Parcel) : this(
        fieldMap = buildMap {
            val mapSize: Int = parcel.readInt()
            repeat(mapSize) {
                val key: AutofillType = AutofillType.valueOf(parcel.readString()!!)
                val listSize: Int = parcel.readInt()
                val ids: MutableList<AutofillId> = MutableList(listSize) {
                    parcel.readParcelable(
                        AutofillId::class.java.classLoader,
                        AutofillId::class.java
                    )!!
                }
                put(key, ids)
            }
        },
        focusedAutofillId = parcel.readParcelable(AutofillId::class.java.classLoader, AutofillId::class.java)!!,
        focusedAutofillPartition = AutofillPartition.valueOf(parcel.readString()!!),
        capabilities = buildList {
            val size: Int = parcel.readInt()
            repeat(size) {
                val account: Uuid = UUID.fromString(parcel.readString()!!).toKotlinUuid()
                val detailsSize: Int = parcel.readInt()
                val details: List<Uuid> = List(detailsSize) {
                    UUID.fromString(parcel.readString()!!).toKotlinUuid()
                }
                val targetUrl: String = parcel.readString()!!
                val capability = AccountCapability(
                    account = account,
                    details = details,
                    targetUrl = targetUrl
                )
                add(capability)
            }
        }
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //Write fieldMap:
        parcel.writeInt(fieldMap.size)
        fieldMap.forEach { (type, ids) ->
            parcel.writeString(type.name)
            parcel.writeInt(ids.size)
            ids.forEach {
                parcel.writeParcelable(it, flags)
            }
        }

        //Write focusedAutofillId:
        parcel.writeParcelable(focusedAutofillId, flags)

        //Write focusedAutofillPartition:
        parcel.writeString(focusedAutofillPartition.name)

        //Write capabilities:
        parcel.writeInt(capabilities.size)
        capabilities.forEach { capability ->
            parcel.writeString(capability.account.toString())
            parcel.writeInt(capability.details.size)
            capability.details.forEach {
                parcel.writeString(it.toString())
            }
            parcel.writeString(capability.targetUrl)
        }
    }


    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<ParcelableAutofillData> {

        override fun createFromParcel(parcel: Parcel): ParcelableAutofillData {
            return ParcelableAutofillData(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableAutofillData?> {
            return arrayOfNulls(size)
        }

    }

}
