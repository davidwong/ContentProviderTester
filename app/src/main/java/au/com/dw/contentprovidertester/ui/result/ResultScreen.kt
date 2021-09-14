package au.com.dw.contentprovidertester.ui.result

import android.util.Log
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import au.com.dw.contentprovidertester.data.model.QueryResult
import au.com.dw.contentprovidertester.ui.QueryUiState
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.common.ProgressIndicator
import au.com.dw.contentprovidertester.ui.navigation.QueryHolder

@Composable
fun ResultScreen(vm: QueryViewModel, onBack: () -> Unit)
{
    val uiState = vm.queryUiState.observeAsState()

    if (uiState.value is QueryUiState.Success<*>) {
        val result = (uiState.value as QueryUiState.Success<*>).data as QueryResult
        TableScreen(result, onBack)
    }
}

/**
 * DEPRECATED - only kept as backup
 */
@Composable
fun ResultScreenInvokeQuery(queryHolder: QueryHolder, vm: QueryViewModel = hiltViewModel(), onBack: () -> Unit)
{
    val context = LocalContext.current
    vm.processQuery(context, queryHolder.uri, queryHolder.projection, queryHolder.selection, queryHolder.selectionArgs, queryHolder.sortOrder)
    /**
     * The UI state determines what to display
     * - on successful query, pass to result screen to show results
     * - if there has been an error or failure (no results for the query), then show an additional
     * snackbar message
     */
    vm.queryUiState.observeAsState().value?.let { uiState ->
        when (uiState) {
            is QueryUiState.Loading -> ProgressIndicator()
            is QueryUiState.Success<*> -> TableScreen((uiState as QueryUiState.Success<*>).data as QueryResult, onBack)
            is QueryUiState.Error -> {
                val error = uiState as QueryUiState.Error
                ShowError(errorMsg = "Error in query: " + error.exception.message, logMessage = "Query error", logThrowable = error.exception, onBack = onBack)
            }
            is QueryUiState.Failure -> {
                val failure = uiState as QueryUiState.Failure
                ShowError(errorMsg = failure.message, logMessage = "Query failure: " + failure.message, logThrowable = null, onBack = onBack)
            }
        }
    }
}

@Composable
fun ShowError(errorMsg: String, logMessage: String?, logThrowable: Throwable?, onBack: () -> Unit)
{
    if (null == logThrowable)
    {
        Log.e("QueryScreen", logMessage!!)
    }
    else {
        Log.e("QueryScreen", logMessage, logThrowable)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Query Results")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                }
            )
        }

    ) { innerPadding ->
        Text(text = errorMsg)
    }
}

//@Preview
//@Composable
//fun PreviewResultScreen() {
//    ResultScreen()
//}