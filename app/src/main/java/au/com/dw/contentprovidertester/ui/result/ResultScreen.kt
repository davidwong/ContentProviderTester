package au.com.dw.contentprovidertester.ui.result

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import au.com.dw.contentprovidertester.data.model.QueryResult
import au.com.dw.contentprovidertester.ui.QueryUiState
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.navigation.QueryHolder
import au.com.dw.contentprovidertester.ui.tableview.*
import com.evrencoskun.tableview.CellAllowClick
import com.evrencoskun.tableview.MyTableView

@Composable
fun ResultScreen(vm: QueryViewModel, onBack: () -> Unit)
{
    val uiState = vm.queryUiState.observeAsState()

    if (uiState.value is QueryUiState.Success<*>) {
        val result = (uiState.value as QueryUiState.Success<*>).data as QueryResult
        TableScreen(result, onBack)
    }
    else
    {
        // this shouldn't happen as successful query should have already been checked for
        // before coming to this screen
        Text("Unable to display results")
    }
}

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
            is QueryUiState.Loading -> showProgressIndicator()
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
fun showProgressIndicator()
{
    Box() {
        CircularProgressIndicator()
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