package au.com.dw.contentprovidertester.ui

import android.app.DownloadManager
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

    private val queryResult = MutableLiveData<QueryDisplayResult<Any>>(QueryDisplayResult.Idle)
    val queryDisplayResult : LiveData<QueryDisplayResult<Any>> = queryResult

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    // onNameChange is an event we're defining that the UI can invoke
    // (events flow up from UI)
    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun processQuery(context: Context, uri: String, projection: String, selection: String, selectionArgs: String, sortOrder: String) {
        // can be launched in a separate asynchronous job
        val queryParam = QueryParam(
            Uri.parse(uri), checkStringArray(projection),
            checkString(selection), checkStringArray(selectionArgs), checkString(sortOrder))
        queryResult.value = loginRepository.processQuery(context, queryParam, emptyList())

        val observerd = queryResult.hasActiveObservers()
        // end repo
//        if (result is Result.Success) {
//            queryResult.value =
//                QueryDisplayResult(success = result.data)
//        } else {
//            queryResult.value =
//                QueryDisplayResult(error = R.string.query_failed)
//        }
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