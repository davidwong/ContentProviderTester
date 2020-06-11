package au.com.dw.contentprovidertester.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import au.com.dw.contentprovidertester.R
import au.com.dw.contentprovidertester.data.Result
import au.com.dw.contentprovidertester.query.ContentResolverQuery
import au.com.dw.contentprovidertester.query.model.QueryParam

class LoginViewModel(private val loginRepository: ContentResolverQuery) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(context: Context, username: String, password: String, selection: String, selectionArgs: String, sortOrder: String) {
        // can be launched in a separate asynchronous job
        val queryParam = QueryParam(username, checkStringArray(password),
            checkString(selection), checkStringArray(selectionArgs), checkString(sortOrder))
        val result = loginRepository.processQuery(context, queryParam, emptyList())

        // end repo
        if (result is Result.Success) {
            _loginResult.value = LoginResult(success = result.data)
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
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