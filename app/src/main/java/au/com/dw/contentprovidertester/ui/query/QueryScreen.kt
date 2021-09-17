package au.com.dw.contentprovidertester.ui.query

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import au.com.dw.contentprovidertester.R
import au.com.dw.contentprovidertester.query.model.QuerySampleFiller
import au.com.dw.contentprovidertester.ui.QueryUiState
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.common.*
import au.com.dw.contentprovidertester.ui.navigation.Screen
import au.com.dw.contentprovidertester.util.LogCompositions
import au.com.dw.contentprovidertester.util.Ref
import kotlinx.coroutines.launch

/**
 * 2 Alternate designs to test which works best:
 *
 * 1
 * Do the query when the query button is clicked, and then check the UI state, i.e.
 * - if query is successful, then navigate to the result screen with the result data
 * - else show error message in snackbar
 * This means navigating in a LaunchedEffect and also resetting the UI state to idle after the
 * results have been displayed, e.g. on back press.
 *
 * 2.
 * The query form screen is a dumb form which passes the form input data to the result screen
 * where the actually content provider query is performed. This is simpler but means any errors
 * will require the user to manually go back to the query form to try again.
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
fun QueryScreenHandler(vm: QueryViewModel, navController: NavController)
{
    // input values to make query, only uri is required and others are optional
//    var uri by rememberSaveable { mutableStateOf("") }
    var uri = rememberSaveable { TextNotEmptyState() }
    var projection by rememberSaveable { mutableStateOf("") }
    var selection by rememberSaveable { mutableStateOf("") }
    var selectionArgs by rememberSaveable { mutableStateOf("") }
    var sortOrder by rememberSaveable { mutableStateOf("") }

    // required for snackbars
    val scaffoldState = rememberScaffoldState()

    val querySampleFiller = QuerySampleFiller()
    // internal value for composable shared by uri and projection dropdown lists - when a CONTENT_URI is
    // selected in the uri dropdown, then the dropdown list for projection is updated to the
    // appropriate column names for that CONTENT_URI
    var projectionLookup by rememberSaveable { mutableStateOf(emptyMap<String, String>()) }
    // for use in URI validation
    val projectionFocusRequest = remember { FocusRequester() }

    // composables for slot API used instead of passing input states so that they can be
    // used for different layouts depending on orientation
    val uriField: @Composable () -> Unit = {
        DropDownValidatingField(uri,
            "uri *",
            ImeAction.Next,
            { projectionFocusRequest.requestFocus() },
            "uri dropdown",
            querySampleFiller.uris,
            { key, items ->
                val uriData = items.get(key)!! as Pair<Uri, Map<String, String>>
                uri.text  = uriData.first.toString()
                projectionLookup = uriData.second
            })
    }
    val projectionField: @Composable () -> Unit = {
        DropDownField(projection, { projection = it },
            "projection",
            "projection dropdown",
            projectionLookup,
            { key, items ->
                projection = addQueryColumn(projection, items.get(key)!! as String)
            })
    }
    val selectionField: @Composable () -> Unit = { PlainField(selection, { selection = it } , "selection") }
    val selectionArgsField: @Composable () -> Unit = { PlainField(selectionArgs, { selectionArgs = it } , "selectionArgs") }
    val sortOrderField: @Composable () -> Unit = { PlainField(sortOrder, { sortOrder = it }  , "sortOrder") }
    val queryButton: @Composable () -> Unit = {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center)
        {
            val context = LocalContext.current
            Button(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.buttonPadding)),
                // invoke the query on the viewmodel
                onClick = { vm.processQuery(context, uri.text, projection, selection, selectionArgs, sortOrder) },
                enabled = uri.isValid
            )
            {
                Text("Query")
            }
        }
    }

    /**
     * The UI state determines what to display
     * - on successful query, pass to result screen to show results
     * - if there has been an error or failure (no results for the query), then show an additional
     * snackbar message
     */
    vm.queryUiState.observeAsState().value?.let { uiState ->
        when (uiState) {
            is QueryUiState.Loading -> {
                Box {
                    LogCompositions("showProgress")
                    // show form first so that the progress bar appears above it
                    QueryScreen(scaffoldState,  uriField, projectionField, selectionField, selectionArgsField, sortOrderField, queryButton)
                    ProgressIndicator()
                }
            }
            is QueryUiState.Success<*> -> {
                // navigate to the results screen, instead of trying to pass the results as a complex
                // parameter the result screen should share the viewmodel and access the result
                // data from it
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Result.route)
                }

            }
            is QueryUiState.Error -> {
                val error = uiState as QueryUiState.Error
                QueryError(scaffoldState = scaffoldState, error.message + ": " + error.exception.message, error.counter, logMessage = "Query error", logThrowable = error.exception)
                QueryScreen(scaffoldState,  uriField, projectionField, selectionField, selectionArgsField, sortOrderField, queryButton)
            }
            is QueryUiState.Failure -> {
                val failure = uiState as QueryUiState.Failure
                QueryError(scaffoldState = scaffoldState, failure.message, failure.counter, logMessage = "Query failure: " + failure.message, logThrowable = null)
                QueryScreen(scaffoldState,  uriField, projectionField, selectionField, selectionArgsField, sortOrderField, queryButton)
            }
            else -> QueryScreen(scaffoldState,  uriField, projectionField, selectionField, selectionArgsField, sortOrderField, queryButton)
        }
    }
}

/**
 * Only need either query1 or query2 lambda for navigation depending on design alternative.
 */
@Composable
fun QueryScreen(scaffoldState: ScaffoldState,
                uriField: @Composable () -> Unit,
                projectionField: @Composable () -> Unit,
                selectionField: @Composable () -> Unit,
                selectionArgsField: @Composable () -> Unit,
                sortOrderField: @Composable () -> Unit,
                queryButton: @Composable () -> Unit)
{
    LogCompositions("showForm")
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

        QueryBodyContent(Modifier.padding(innerPadding),
            uriField,
            projectionField,
            selectionField,
            selectionArgsField,
            sortOrderField,
            queryButton)
    }
}

class SnackbarRef(var value: Int)
/**
 * Show snackbar when there has been a query error or the query has no results to show.
 *
 * todo
 * On orientation change when the snackbar has already show, the snackbar will popup again since
 * the UI state is still set to error or failure. Tried resetting UI state to idle after the snackbar
 * is dismissed, but then the whole form is refreshed and the URI field is wiped
 *
 */
@Composable
fun QueryError(scaffoldState: ScaffoldState, errorMsg: String, errorCount: Int, logMessage: String?, logThrowable: Throwable?)
{
    LogCompositions("showError")
    if (null == logThrowable)
    {
        Log.e("QueryScreen", logMessage!!)
    }
    else {
        Log.e("QueryScreen", logMessage, logThrowable)
    }

    // Note that the error count is used as the key as it is unique for each query.
    // This ensures that the snackbar error message gets displayed for each separate query even if
    // the result is the same error/failure.
    LaunchedEffect(errorCount) {
        // can't use LogCompositions here since it is only allowed inside composable function
        Log.d("Compositions: ", "snackbar")

        val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
            message = "Error in query: " + errorMsg
        )

    }

}

@Composable
fun QueryBodyContent(modifier: Modifier = Modifier,
                     uriField: @Composable () -> Unit,
                     projectinField: @Composable () -> Unit,
                     selectionField: @Composable () -> Unit,
                     selectionArgsField: @Composable () -> Unit,
                     sortOrderField: @Composable () -> Unit,
                     queryButton: @Composable () -> Unit) {
    val isLandscape = (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE)
    if (isLandscape)
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            Row(modifier = modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1.0f)) {
                    uriField()
                    projectinField()
                }
                Column(modifier = Modifier.weight(1.0f)) {
                    selectionField()
                    selectionArgsField()
                    sortOrderField()
                }
            }
            queryButton()
        }
    else
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            uriField()
            projectinField()
            selectionField()
            selectionArgsField()
            sortOrderField()
            queryButton()
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
//    val querySampleFiller = QuerySampleFiller()
//
//    QueryScreen(rememberScaffoldState(), querySampleFiller) { c, s1, s2, s3, s4, s5 -> Unit }
//}

