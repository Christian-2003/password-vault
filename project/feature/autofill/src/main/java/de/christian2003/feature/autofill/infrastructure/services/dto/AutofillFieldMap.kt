package de.christian2003.feature.autofill.infrastructure.services.dto

import android.os.Parcel
import android.os.Parcelable
import android.view.autofill.AutofillId
import de.christian2003.feature.autofill.domain.entities.AutofillType


internal class AutofillFieldMap(
    val map: Map<AutofillId, List<AutofillType>>
): Parcelable {

    constructor(parcel: Parcel) : this(
        buildMap {
            val size = parcel.readInt()
            repeat(size) {
                val key = parcel.readParcelable(AutofillId::class.java.classLoader, AutofillId::class.java)!!

                val listSize = parcel.readInt()
                val list = MutableList(listSize) {
                    AutofillType.valueOf(parcel.readString()!!)
                }

                put(key, list)
            }
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(map.size)

        map.forEach { (key, value) ->
            parcel.writeParcelable(key, flags)

            parcel.writeInt(value.size)
            value.forEach {
                parcel.writeString(it.name) // safer than ordinal
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<AutofillFieldMap> {
        override fun createFromParcel(parcel: Parcel): AutofillFieldMap {
            return AutofillFieldMap(parcel)
        }

        override fun newArray(size: Int): Array<AutofillFieldMap?> {
            return arrayOfNulls(size)
        }
    }

}
