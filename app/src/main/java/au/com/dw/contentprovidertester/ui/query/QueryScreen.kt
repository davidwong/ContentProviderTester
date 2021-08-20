package au.com.dw.contentprovidertester.ui.query

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import au.com.dw.contentprovidertester.query.model.QuerySampleFiller
import au.com.dw.contentprovidertester.ui.QueryUiState
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.navigation.Screen

@Composable
fun QueryScreen(vm: QueryViewModel, navController: NavController)
{
    // required for snackbars
    val scaffoldState = rememberScaffoldState()

    /**
     * The UI state determines what to display
     * - on successful query, pass to result screen to show results
     * - if there has been an error or failure (no results for the query), then show an additional
     * snackbar message
     */
    vm.queryUiState.observeAsState().value?.let { uiState ->
        when (uiState) {
            is QueryUiState.Loading -> showProgressIndicator(scaffoldState, vm::processQuery)
            is QueryUiState.Success<*> -> navController.navigate(Screen.Result.route)
            is QueryUiState.Error -> {
                val error = uiState as QueryUiState.Error
                ShowError(scaffoldState = scaffoldState, errorMsg = "Error in query: " + error.exception.message, logMessage = "Query error", logThrowable = error.exception)
                showForm(scaffoldState = scaffoldState, vm::processQuery)
            }
            is QueryUiState.Failure -> {
                val failure = uiState as QueryUiState.Failure
                ShowError(scaffoldState = scaffoldState, errorMsg = failure.message, logMessage = "Query failure: " + failure.message, logThrowable = null)
                showForm(scaffoldState = scaffoldState, vm::processQuery)
            }
            else -> showForm(scaffoldState = scaffoldState, vm::processQuery)
        }
    }
}

@Composable
fun showForm(scaffoldState: ScaffoldState, onQuery: (Context, String, String, String, String, String) -> Unit)
{
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
        QueryBodyContent(Modifier.padding(innerPadding), QuerySampleFiller(), onQuery)
    }
}

@Composable
fun showProgressIndicator(scaffoldState: ScaffoldState, onQuery: (Context, String, String, String, String, String) -> Unit)
{
    Box() {
        showForm(scaffoldState, onQuery)
        CircularProgressIndicator()
    }
}

@Composable
fun ShowError(scaffoldState: ScaffoldState, errorMsg: String, logMessage: String?, logThrowable: Throwable?)
{
    if (null == logThrowable)
    {
        Log.e("QueryScreen", logMessage!!)
    }
    else {
        Log.e("QueryScreen", logMessage, logThrowable)
    }

    // `LaunchedEffect` will cancel and re-launch if
    // `scaffoldState.snackbarHostState` changes
    LaunchedEffect(scaffoldState.snackbarHostState) {
        // Show snackbar using a coroutine, when the coroutine is cancelled the
        // snackbar will automatically dismiss. This coroutine will cancel whenever
        // `state.hasError` is false, and only start when `state.hasError` is true
        // (due to the above if-check), or if `scaffoldState.snackbarHostState` changes.
        scaffoldState.snackbarHostState.showSnackbar(
            message = "Error in query: " + errorMsg
        )
    }
}

@Composable
fun QueryBodyContent(modifier: Modifier = Modifier, querySampleFiller: QuerySampleFiller, onQuery: (Context, String, String, String, String, String) -> Unit)
{
    Column {
        // internal value for composable shared by uri and projection dropdown lists - when a CONTENT_URI is
        // selected in the uri dropdown, then the dropdown list for projection is updated to the
        // appropriate column names for that CONTENT_URI
        var projectionLookup by remember { mutableStateOf(emptyMap<String, String>()) }

        // input values to make query, only uri is required and others are optional
        var uri by remember { mutableStateOf("") }
        var projection by remember { mutableStateOf("") }
        var selection by remember { mutableStateOf("") }
        var selectionArgs by remember { mutableStateOf("") }
        var sortOrder by remember { mutableStateOf("") }

        DropDownField(uri, { uri = it },"uri",
            querySampleFiller.uris,
            { key, items ->
                val uriData = items.get(key)!! as Pair<Uri, Map<String, String>>
                uri  = uriData.first.toString()
                projectionLookup = uriData.second
            })

        DropDownField(projection, { projection = it },"projection",
            projectionLookup,
            { key, items ->
                projection = addQueryColumn(projection, items.get(key)!! as String)
            })

        PlainField(selection, { selection = it } , "selection")
        PlainField(selectionArgs, { selectionArgs = it } , "selectionArgs")
        PlainField(sortOrder, { sortOrder = it } , "sortOrder")

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

@Composable
fun PlainField(fieldValue: String, onFieldChange: (String) -> Unit, fieldLabel: String) {
    TextField(
        value = fieldValue,
        onValueChange = onFieldChange,
        label = { Text(fieldLabel)}
    )
}

// for convenience similate a drop down list (not currently available in the compose library) e.g. for commonly
// used query URI's

/**
 * The first 3 parameters are for the text field (same as for PlainText) and the drop down parameters
 * are for the dropdown list with a map of the labels to display and the values to use when an item
 * from the list is selected.
 */
@Composable
fun DropDownField(fieldValue: String,
                  onFieldChange: (String) -> Unit,
                  fieldLabel: String,
                  dropDownItems: Map<String, Any>,
                  onDropDownSelected: (String, Map<String, Any>) -> Unit) {
    Box() {
        var expanded by remember { mutableStateOf(false) }
        val icon = if (expanded)
            Icons.Filled.Search
        else
            Icons.Filled.ArrowDropDown

        OutlinedTextField(
            value = fieldValue,
            onValueChange = onFieldChange,
//            modifier = Modifier.fillMaxWidth(),
            label = { Text(fieldLabel) },
            trailingIcon = {
                Icon(icon, "contentDescription", Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            // can't use map directly here as will get error message about not being inside
            // a composable, so use a separate lookup instead
            dropDownItems.keys.forEach { label ->
                DropdownMenuItem(onClick = {
                    onDropDownSelected(label, dropDownItems)
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}

/**
 * For use in fields where we want to add an item to the field, instead of replacing it.
 */
fun addQueryColumn(projection : String, newValue : String): String {
    if (TextUtils.isEmpty(projection))
    {
        return newValue
    }
    else{
        return projection + ", " + newValue
    }
}

//@Preview
//@Composable
//fun PreviewQueryScreen() {
//    QueryScreen()
//}