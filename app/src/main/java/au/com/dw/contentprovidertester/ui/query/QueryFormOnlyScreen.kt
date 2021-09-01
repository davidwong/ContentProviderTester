package au.com.dw.contentprovidertester.ui.query

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import au.com.dw.contentprovidertester.query.model.QuerySampleFiller
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.escapeUriString

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
 * Query screen for design 2.
 */
@Composable
fun QueryFormOnlyScreen(onQuery: (String, String?, String?, String?, String?) -> Unit)
{
    showForm2(onQuery)
}

/**
 * Only need either query1 or query2 lambda for navigation depending on design alternative.
 */
@Composable
fun showForm2(onQuery: (String, String?, String?, String?, String?) -> Unit)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Content Provider Query")
                }
            )
        }
    ) { innerPadding ->
        QueryBodyContent2(Modifier.padding(innerPadding), QuerySampleFiller(), onQuery)
    }
}

/**
 * Only need either query1 or query2 lambda for navigation depending on design alternative.
 */
@Composable
fun QueryBodyContent2(modifier: Modifier = Modifier, querySampleFiller: QuerySampleFiller, onQuery: (String, String?, String?, String?, String?) -> Unit)
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
            Button(
                // pass on input params to the results screen
//                onClick = { onQuery?.let { it(escapeUriString(uri), projection, selection, selectionArgs, sortOrder) } }
                onClick = { onQuery(escapeUriString(uri), projection, selection, selectionArgs, sortOrder) }
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
//    QueryScreen({ s: String, s1: String?, s2: String?, s3: String?, s4: String? -> })
//}