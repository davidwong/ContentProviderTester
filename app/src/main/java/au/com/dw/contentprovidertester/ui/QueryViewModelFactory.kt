package au.com.dw.contentprovidertester.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import au.com.dw.contentprovidertester.query.ContentResolverQuery

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class QueryViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QueryViewModel::class.java)) {
            return QueryViewModel(
                loginRepository = ContentResolverQuery()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}