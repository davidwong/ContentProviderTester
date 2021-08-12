package au.com.dw.contentprovidertester.ui

/**
 * The query results to display to the user or error message.
 */
sealed class QueryUiState<out T : Any> {

        object Idle : QueryUiState<Nothing>()

        object Loading : QueryUiState<Nothing>()

        data class Success<out T : Any>(val data: T) : QueryUiState<T>()

        data class Failure(val message: String) : QueryUiState<Nothing>()

        data class Error(val exception: Exception) : QueryUiState<Nothing>()

        override fun toString(): String {
                return when (this) {
                        is Idle -> "Idle"
                        is Loading -> "Loading"
                        is Success<*> -> "Success[data=$data]"
                        is Failure -> "Failure[message=$message]"
                        is Error -> "Error[exception=$exception]"
                }
        }
}