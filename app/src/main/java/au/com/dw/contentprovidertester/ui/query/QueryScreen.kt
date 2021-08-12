package au.com.dw.contentprovidertester.ui.query

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import au.com.dw.contentprovidertester.ui.QueryUiState
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.navigation.Screen

@Composable
fun QueryScreen(vm: QueryViewModel, navController: NavController)
{
    val uiState = vm.queryUiState.observeAsState()
    val scaffoldState = rememberScaffoldState()

    if (uiState.value is QueryUiState.Success<*>)
    {
        navController.navigate(Screen.Result.route)
    }
    else {
        if (uiState.value is QueryUiState.Error) {
            val error = uiState.value as QueryUiState.Error
            Log.e("QueryScreen", "Query error", error.exception)

            // `LaunchedEffect` will cancel and re-launch if
            // `scaffoldState.snackbarHostState` changes
            LaunchedEffect(scaffoldState.snackbarHostState) {
                // Show snackbar using a coroutine, when the coroutine is cancelled the
                // snackbar will automatically dismiss. This coroutine will cancel whenever
                // `state.hasError` is false, and only start when `state.hasError` is true
                // (due to the above if-check), or if `scaffoldState.snackbarHostState` changes.
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Error in query: " + error.exception.message
                )
            }
        } else if (uiState.value is QueryUiState.Failure) {
            val failure = uiState.value as QueryUiState.Failure
            Log.e("QueryScreen", "Query failure: " + failure.message)

            LaunchedEffect(scaffoldState.snackbarHostState) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = failure.message
                )
            }
        }
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Content Provider Query")
                    }
                )
            }
        ) { innerPadding ->
            QueryBodyContent(Modifier.padding(innerPadding), vm::processQuery)
        }

    }
}


@Composable
fun QueryBodyContent(modifier: Modifier = Modifier, onQuery: (Context, String, String, String, String, String) -> Unit)
{
    Column {
        var uri by remember { mutableStateOf("") }
        TextField(
            value = uri,
            onValueChange = { uri = it },
            label = { Text("uri")}
        )
        var projection by remember { mutableStateOf("") }
        TextField(
            value = projection,
            onValueChange = { projection = it },
            label = { Text("projection")}
        )
        var selection by remember { mutableStateOf("") }
        TextField(
            value = selection,
            onValueChange = { selection = it },
            label = { Text("selection")}
        )
        var selectionArgs by remember { mutableStateOf("") }
        TextField(
            value = selectionArgs,
            onValueChange = { selectionArgs = it },
            label = { Text("selectionArgs")}
        )
        var sortOrder by remember { mutableStateOf("") }
        TextField(
            value = sortOrder,
            onValueChange = { sortOrder = it },
            label = { Text("sortOrder")}
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center)
        {
            val context = LocalContext.current
            Button(
                onClick = { onQuery(context, uri, projection, selection, selectionArgs, sortOrder) }
            )
            {
                Text("Query")
            }
        }
    }
}

//@Preview
//@Composable
//fun PreviewQueryScreen() {
//    QueryScreen()
//}