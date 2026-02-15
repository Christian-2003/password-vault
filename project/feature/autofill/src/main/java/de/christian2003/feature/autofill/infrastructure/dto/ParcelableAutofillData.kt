package de.christian2003.feature.autofill.infrastructure.dto

import android.os.Parcel
import android.os.Parcelable
import android.view.autofill.AutofillId
import de.christian2003.data.accounts.domain.entities.AccountCapability
import de.christian2003.feature.autofill.domain.entities.AutofillType
import java.util.UUID
import kotlin.uuid.Uuid
import kotlin.uuid.toKotlinUuid


internal class ParcelableAutofillData(
    val fieldMap: Map<AutofillId, List<AutofillType>>,
    val capabilities: List<AccountCapability>
): Parcelable {

    constructor(parcel: Parcel) : this(
        fieldMap = buildMap {
            val size = parcel.readInt()
            repeat(size) {
                val key = parcel.readParcelable(AutofillId::class.java.classLoader, AutofillId::class.java)!!

                val listSize = parcel.readInt()
                val list = MutableList(listSize) {
                    AutofillType.valueOf(parcel.readString()!!)
                }

                put(key, list)
            }
        },
        capabilities = buildList {
            val size = parcel.readInt()
            repeat(size) {
                val account = UUID.fromString(parcel.readString()!!)

                val detailsSize = parcel.readInt()
                val details: List<Uuid> = List(detailsSize) {
                    UUID.fromString(parcel.readString()!!).toKotlinUuid()
                }

                val targetUrl = parcel.readString()!!

                add(
                    AccountCapability(
                        account = account.toKotlinUuid(),
                        details = details,
                        targetUrl = targetUrl
                    )
                )
            }
        }
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // ---- Write Map ----
        parcel.writeInt(fieldMap.size)
        fieldMap.forEach { (key, value) ->
            parcel.writeParcelable(key, flags)

            parcel.writeInt(value.size)
            value.forEach {
                parcel.writeString(it.name)
            }
        }

        // ---- Write AccountCapabilities ----
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
