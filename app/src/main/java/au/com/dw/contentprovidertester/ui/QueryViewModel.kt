package au.com.dw.contentprovidertester.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import au.com.dw.contentprovidertester.R
import au.com.dw.contentprovidertester.data.Result
import au.com.dw.contentprovidertester.query.ContentResolverQuery
import au.com.dw.contentprovidertester.query.model.QueryParam

class QueryViewModel(private val loginRepository: ContentResolverQuery) : ViewModel() {

    private val queryResult = MutableLiveData<QueryDisplayResult>()
    val queryDisplayResult: LiveData<QueryDisplayResult> = queryResult

    fun processQuery(context: Context, username: String, password: String, selection: String, selectionArgs: String, sortOrder: String) {
        // can be launched in a separate asynchronous job
        val queryParam = QueryParam(
            Uri.parse(username), checkStringArray(password),
            checkString(selection), checkStringArray(selectionArgs), checkString(sortOrder))
        val result = loginRepository.processQuery(context, queryParam, emptyList())

        // end repo
        if (result is Result.Success) {
            queryResult.value =
                QueryDisplayResult(success = result.data)
        } else {
            queryResult.value =
                QueryDisplayResult(error = R.string.query_failed)
        }
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

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return   username.isNotBlank()
    }
}