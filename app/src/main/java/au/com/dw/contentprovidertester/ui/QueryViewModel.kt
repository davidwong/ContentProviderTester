package au.com.dw.contentprovidertester.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.dw.contentprovidertester.query.ContentResolverQuery
import au.com.dw.contentprovidertester.query.model.QueryParam

class QueryViewModel(private val loginRepository: ContentResolverQuery) : ViewModel() {

    private val queryResult = MutableLiveData<QueryUiState<Any>>(QueryUiState.Idle)
    val queryUiState : LiveData<QueryUiState<Any>> = queryResult

    fun processQuery(context: Context, uri: String, projection: String, selection: String, selectionArgs: String, sortOrder: String) {
        // can be launched in a separate asynchronous job
        val queryParam = QueryParam(
            Uri.parse(uri), checkStringArray(projection),
            checkString(selection), checkStringArray(selectionArgs), checkString(sortOrder))
        queryResult.value = loginRepository.processQuery(context, queryParam, emptyList())
    }

    private fun checkString(value: String): String?
    {
        if (value.isNotBlank())
            return value
        else
            return null
    }

    private fun checkStringArray(arrayString: String): Array<String>?
    {
        if (arrayString.isNotBlank())
            return arrayString.split(",").toTypedArray()
        else
            return null
    }

}