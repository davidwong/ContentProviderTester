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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import au.com.dw.contentprovidertester.query.model.QuerySampleFiller
import au.com.dw.contentprovidertester.ui.QueryUiState
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.navigation.Screen

/**
 * 2 Alternate designs to test which works best:
 *
 * 1
 * Do the query when the query button is clicked, and then check the UI state, i.e.
 * - if query is successful, then navigate to the result screen with the result data
 * - else show error message in snackbar
 * This means navigating in a LaunchedEffect and also resetting the UI state to idle afterwards.
 *
 * 2.
 * The query form screen is a dumb form which passes the form input data to the result screen
 * where the actually content provider query is performed.
 *
 * Note:
 * Another possibility is to do the query when the query button is clicked, and navigate from the
 * viewmodel. Not sure whether navigating in the viewmodel would be good design, as may mean navigation
 * is spread across viewmodels and composables.
 * https://medium.com/google-developer-experts/modular-navigation-with-jetpack-compose-fda9f6b2bef7
 * https://funkymuse.dev/posts/compose_hilt_mm/
 */

/**
 * Query screen for design 1.
 */
@Composable
fun QueryScreen1(vm: QueryViewModel, navController: NavController)
{
    // attempt 1

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
            is QueryUiState.Success<*> -> {

                LaunchedEffect(uiState == QueryUiState.Test) {
                    navController.navigate(Screen.Result1.route)
                }

            }
            is QueryUiState.Error -> {
                val error = uiState as QueryUiState.Error
                ShowError(scaffoldState = scaffoldState, errorMsg = "Error in query: " + error.exception.message, logMessage = "Query error", logThrowable = error.exception)
                showForm(scaffoldState = scaffoldState, vm::processQuery, null)
            }
            is QueryUiState.Failure -> {
                val failure = uiState as QueryUiState.Failure
                ShowError(scaffoldState = scaffoldState, errorMsg = failure.message, logMessage = "Query failure: " + failure.message, logThrowable = null)
                showForm(scaffoldState = scaffoldState, vm::processQuery, null)
            }
            else -> showForm(scaffoldState = scaffoldState, vm::processQuery, null)
        }
    }
}

/**
 * Query screen for design 2.
 */
@Composable
fun QueryScreen2(onQuery2: (String, String?, String?, String?, String?) -> Unit)
{
    // scaffoldState not used and only one of the query lambda's is used, only put in to satisfy function signature
    val scaffoldState = rememberScaffoldState()
    val vm: QueryViewModel = hiltViewModel()
    showForm(scaffoldState, null, onQuery2)
}

/**
 * Only need either query1 or query2 lambda for navigation depending on design alternative.
 */
@Composable
fun showForm(scaffoldState: ScaffoldState, onQuery1: ((Context, String, String?, String?, String?, String?) -> Unit)?, onQuery2: ((String, String?, String?, String?, String?) -> Unit)?)
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
        QueryBodyContent(Modifier.padding(innerPadding), QuerySampleFiller(), onQuery1, onQuery2)
    }
}

@Composable
fun showProgressIndicator(scaffoldState: ScaffoldState, onQuery1: (Context, String, String?, String?, String?, String?) -> Unit)
{
    Box() {
        // show form first so that the progress bar appears above it
        showForm(scaffoldState, onQuery1, null)
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

/**
 * Only need either query1 or query2 lambda for navigation depending on design alternative.
 */
@Composable
fun QueryBodyContent(modifier: Modifier = Modifier, querySampleFiller: QuerySampleFiller, onQuery1: ((Context, String, String?, String?, String?, String?) -> Unit)?, onQuery2: ((String, String?, String?, String?, String?) -> Unit)?)
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
                // design 1
                // invoke the query on the viewmodel
                onClick = { onQuery1?.let { it(context, uri, projection, selection, selectionArgs, sortOrder) } }

                // design 2
                // pass on input params to the results screen
//                onClick = { onQuery2?.let { it(escapeUriString(uri), projection, selection, selectionArgs, sortOrder) } }
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
//    QueryScreen({ s: String, s1: String?, s2: String?, s3: String?, s4: String? -> })
//}