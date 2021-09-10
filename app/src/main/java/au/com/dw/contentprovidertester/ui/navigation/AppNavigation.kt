package au.com.dw.contentprovidertester.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import au.com.dw.contentprovidertester.theme.AppTheme
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.query.QueryFormOnlyScreen
import au.com.dw.contentprovidertester.ui.query.QueryScreenHandler
import au.com.dw.contentprovidertester.ui.result.ResultScreen
import au.com.dw.contentprovidertester.ui.result.ResultScreenInvokeQuery
import au.com.dw.contentprovidertester.ui.unEscapeUriString

@Composable
fun AppNavigation() {
    AppTheme {
        val navController = rememberNavController()
        val vm: QueryViewModel = hiltViewModel()
        // choose Query or QueryFormOnly as start destination
        NavHost(navController, startDestination = Screen.Query.route) {
            composable(route = Screen.Query.route) {
                QueryScreenHandler(vm, navController)
            }
            composable(route = Screen.Result.route) {
                ResultScreen(vm) {
                    // reset UI state to clear data if go back to query screen for another query
                    vm.reset()
                    navController.navigateUp()
                }
            }
            composable(route = Screen.QueryFormOnly.route) {
                QueryFormOnlyScreen() { uri, projection, selection, selectionArgs, sortOrder ->
                    navController.navigate(
                        Screen.ResultInvokeQuery.routeWithParams(
                            uri,
                            projection,
                            selection,
                            selectionArgs,
                            sortOrder
                        )
                    )
                }
            }
            // don't need add list of arguments to composable parameters, since they are all default strings
            composable(route = Screen.ResultInvokeQuery.route) { backStackEntry ->
                val uri = backStackEntry.arguments?.getString("uri")
                requireNotNull(uri) { "URI not found for query" }
                val projection = backStackEntry.arguments?.getString("projection")
                val selection = backStackEntry.arguments?.getString("selection")
                val selectionArgs = backStackEntry.arguments?.getString("selectionArgs")
                val sortOrder = backStackEntry.arguments?.getString("sortOrder")
                // unescaping URI may not actually be necessary, as seems to be done already
                val queryHolder = QueryHolder(
                    unEscapeUriString(uri),
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )
                ResultScreenInvokeQuery(queryHolder) { navController.navigateUp() }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Query: Screen("query")
    object QueryFormOnly: Screen("queryformonly")
    object Result: Screen("result")
    object ResultInvokeQuery: Screen("resultinvokequery/{uri}?projection={projection}&selection={selection}&selectionArgs={selectionArgs}&sortOrder={sortOrder}") {
        fun routeWithParams(uri: String,
                  projection: String?,
                  selection: String?,
                  selectionArgs: String?,
                  sortOrder: String?) = "resultinvokequery/${uri}?projection=$projection&selection=$selection&selectionArgs=$selectionArgs&sortOrder=$sortOrder"
    }
}

data class QueryHolder(
    val uri: String,
    val projection: String?,
    val selection: String?,
    val selectionArgs: String?,
    val sortOrder: String?
)