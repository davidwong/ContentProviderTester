package au.com.dw.contentprovidertester.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.dw.contentprovidertester.query.ContentResolverQuery
import au.com.dw.contentprovidertester.query.model.QueryParam
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

    fun processQuery(context: Context, uri: String, projection: String?, selection: String?, selectionArgs: String?, sortOrder: String?) {
        queryResult.value = QueryUiState.Loading

        // for testing progress bar
//        Handler(Looper.getMainLooper()).postDelayed({
//            val queryParam = QueryParam(
//                Uri.parse(uri), checkStringArray(projection),
//                checkString(selection), checkStringArray(selectionArgs), checkString(sortOrder)
//            )
//            queryResult.value = contentResolverQuery.processQuery(context, queryParam, emptyList())
//        }, 3000)

        // can also be launched in a separate asynchronous job
        val unescapedUri = unEscapeUriString(uri)
        val queryParam = QueryParam(
            Uri.parse(uri), checkQueryStringArray(projection),
            checkQueryString(selection), checkQueryStringArray(selectionArgs), checkQueryString(sortOrder)
        )
        queryResult.value = contentResolverQuery.processQuery(context, queryParam, emptyList())
    }

    /**
     * Reset the UI state to clear data from previous queries.
     */
    fun reset()
    {
        queryResult.value = QueryUiState.Idle
    }
}