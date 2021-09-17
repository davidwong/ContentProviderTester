package au.com.dw.contentprovidertester.query.model

/**
 * The result of a content provider query.
 */
sealed class QueryResultHolder<out T : Any> {

        data class Success<out T : Any>(val data: T) : QueryResultHolder<T>()

        // A query that returns no results is considered a failure in terms of display data
        data class Failure(val message: String) : QueryResultHolder<Nothing>()

        data class Error(val message: String, val exception: Exception) : QueryResultHolder<Nothing>()

        override fun toString(): String {
                return when (this) {
                        is Success<*> -> "Success[data=$data]"
                        is Failure -> "Failure[message=$message]"
                        is Error -> "Error[message=$message, exception=$exception]"
                }
        }
}