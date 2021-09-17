package au.com.dw.contentprovidertester.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.dw.contentprovidertester.query.ContentResolverQuery
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.query.model.QueryResultHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
//class QueryViewModel(private val loginRepository: ContentResolverQuery) : ViewModel() {
class QueryViewModel @Inject constructor(
//    private val savedStateHandle: SavedStateHandle,
    private val contentResolverQuery: ContentResolverQuery
    ) : ViewModel() {

    private val queryResult = MutableLiveData<QueryUiState<Any>>(QueryUiState.Idle)
    val queryUiState : LiveData<QueryUiState<Any>> = queryResult

    /**
     * An extra error counter is added to the UI state when there is an error or failure. This is because
     * it can be used as the key for the LaunchedEffect that displays the error
     * message in a snackbar. This ensures that the snackbar is shown for every query even if it is
     * the same error/failure.
     */
    private var errorCount = 0

    fun processQuery(context: Context, uri: String, projection: String?, selection: String?, selectionArgs: String?, sortOrder: String?) {
        queryResult.value = QueryUiState.Loading

        // for testing progress bar
//        Handler(Looper.getMainLooper()).postDelayed({
//            val queryParam = QueryParam(
//                Uri.parse(uri), checkQueryStringArray(projection),
//                checkQueryString(selection), checkQueryStringArray(selectionArgs), checkQueryString(sortOrder)
//            )
//            queryResult.value = contentResolverQuery.processQuery(context, queryParam, emptyList())
//        }, 3000)

        // can also be launched in a separate asynchronous job

        // only needed if URI string was escaped before this function was invoked
//        val unescapedUri = unEscapeUriString(uri)

        val queryParam = QueryParam(
            Uri.parse(uri),
            checkQueryStringArray(projection),
            checkQueryString(selection),
            checkQueryStringArray(selectionArgs),
            checkQueryString(sortOrder)
        )
        val resultHolder = contentResolverQuery.processQuery(context, queryParam, emptyList())
        when (resultHolder) {
            is QueryResultHolder.Success -> queryResult.value = QueryUiState.Success(resultHolder.data)
            is QueryResultHolder.Failure -> queryResult.value = QueryUiState.Failure(resultHolder.message, errorCount++)
            is QueryResultHolder.Error -> queryResult.value = QueryUiState.Error(resultHolder.message, resultHolder.exception, errorCount++)
        }
    }

    /**
     * Reset the UI state to clear data from previous queries.
     */
    fun reset()
    {
        queryResult.value = QueryUiState.Idle
    }
}