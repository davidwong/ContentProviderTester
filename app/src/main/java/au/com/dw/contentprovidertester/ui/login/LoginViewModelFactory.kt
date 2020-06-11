package au.com.dw.contentprovidertester.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.com.dw.contentprovidertester.query.ContentResolverQuery
import au.com.dw.contentprovidertester.query.JsonQuery

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                    loginRepository = ContentResolverQuery()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}