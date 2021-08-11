package au.com.dw.contentprovidertester.ui

import au.com.dw.contentprovidertester.data.Result
import au.com.dw.contentprovidertester.data.model.QueryResult

/**
 * The query results to display to the user or error message.
 */
//data class QueryDisplayResult(
//        val success: QueryResult? = null,
//        val error: Int? = null
//)

sealed class QueryDisplayResult<out T : Any> {

        object Idle : QueryDisplayResult<Nothing>()

        object Loading : QueryDisplayResult<Nothing>()

        data class Success<out T : Any>(val data: T) : QueryDisplayResult<T>()
        data class Error(val exception: Exception) : QueryDisplayResult<Nothing>()

        override fun toString(): String {
                return when (this) {
                        is Idle -> "Idle"
                        is Loading -> "Loading"
                        is Success<*> -> "Success[data=$data]"
                        is Error -> "Error[exception=$exception]"
                }
        }
}