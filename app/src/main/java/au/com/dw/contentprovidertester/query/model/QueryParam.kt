package au.com.dw.contentprovidertester.query.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Parameters to pass to ContentResolver.query(). Leave projection as null to get all columns
 * in the results.
 */
data class QueryParam (
    val uri: String,
    val projection: Array<String>? = null,
    val selection: String? = null,
    val selectionArgs: Array<String>? = null,
    val sortOrder: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.createStringArray(),
        parcel.readString(),
        parcel.createStringArray(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uri)
        parcel.writeStringArray(projection)
        parcel.writeString(selection)
        parcel.writeStringArray(selectionArgs)
        parcel.writeString(sortOrder)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QueryParam> {
        override fun createFromParcel(parcel: Parcel): QueryParam {
            return QueryParam(parcel)
        }

        override fun newArray(size: Int): Array<QueryParam?> {
            return arrayOfNulls(size)
        }
    }
}