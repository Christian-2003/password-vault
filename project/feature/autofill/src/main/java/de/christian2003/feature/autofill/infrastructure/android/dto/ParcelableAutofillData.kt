package de.christian2003.feature.autofill.infrastructure.android.dto

import android.os.Parcel
import android.os.Parcelable
import android.view.autofill.AutofillId
import de.christian2003.data.accounts.domain.entities.AccountCapability
import de.christian2003.feature.autofill.domain.entities.AutofillType
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


internal class ParcelableAutofillData(
    val fieldMap: Map<AutofillType, List<AutofillId>>,
    val capabilities: List<AccountCapability>
): Parcelable {

    constructor(parcel: Parcel) : this(
        fieldMap = buildMap {
            val mapSize = parcel.readInt()
            repeat(mapSize) {
                val key = AutofillType.valueOf(parcel.readString()!!)

                val listSize = parcel.readInt()
                val ids = MutableList(listSize) {
                    parcel.readParcelable<AutofillId>(
                        AutofillId::class.java.classLoader
                    )!!
                }

                put(key, ids)
            }
        },
        capabilities = buildList {
            val size = parcel.readInt()
            repeat(size) {
                val account: Uuid = UUID.fromString(parcel.readString()!!).toKotlinUuid()

                val detailsSize = parcel.readInt()
                val details: List<Uuid> = List(detailsSize) {
                    UUID.fromString(parcel.readString()!!).toKotlinUuid()
                }

                val targetUrl = parcel.readString()!!

                add(
                    AccountCapability(
                        account = account,
                        details = details,
                        targetUrl = targetUrl
                    )
                )
            }
        }
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // ---- Write Map<AutofillType, List<AutofillId>> ----
        parcel.writeInt(fieldMap.size)
        fieldMap.forEach { (type, ids) ->
            parcel.writeString(type.name)

            parcel.writeInt(ids.size)
            ids.forEach {
                parcel.writeParcelable(it, flags)
            }
        }

        // ---- Write List<AccountCapability> ----
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
