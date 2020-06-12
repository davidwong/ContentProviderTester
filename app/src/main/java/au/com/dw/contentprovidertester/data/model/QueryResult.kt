package au.com.dw.contentprovidertester.data.model

import android.os.Parcel
import android.os.Parcelable
import au.com.dw.contentprovidertester.query.model.QueryParam
import kotlinx.android.parcel.Parcelize

data class QueryResult ( val params: QueryParam, val results: List<Map<String, Any>>, val executionTime: Long)