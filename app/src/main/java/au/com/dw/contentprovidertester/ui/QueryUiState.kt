package au.com.dw.contentprovidertester.ui

/**
 * The states for the query screen which determines whether to display results to the user or
 * show an error or no result message.
 */
sealed class QueryUiState<out T : Any> {

        object Idle : QueryUiState<Nothing>()

        object Loading : QueryUiState<Nothing>()

        data class Success<out T : Any>(val data: T) : QueryUiState<T>()

        // A query that returns no results is considered a failure in terms of display data
        data class Failure(val message: String) : QueryUiState<Nothing>()

        data class Error(val message: String, val exception: Exception) : QueryUiState<Nothing>()

        override fun toString(): String {
                return when (this) {
                        is Idle -> "Idle"
                        is Loading -> "Loading"
                        is Success<*> -> "Success[data=$data]"
                        is Failure -> "Failure[message=$message]"
                        is Error -> "Error[message=$message, exception=$exception]"
                }
        }
}