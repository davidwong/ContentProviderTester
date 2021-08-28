package au.com.dw.contentprovidertester.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.query.QueryScreen
import au.com.dw.contentprovidertester.ui.result.ResultScreen
import au.com.dw.contentprovidertester.ui.unEscapeUriString

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Query.route) {
        composable(route = Screen.Query.route) {
//            QueryScreen({ navController.navigate(Screen.Result.route) })
            QueryScreen(
            {
                uri, projection, selection, selectionArgs, sortOrder ->
                val theRoute = Screen.Result.routeWithParams(uri, projection, selection, selectionArgs, sortOrder)
                navController.navigate(theRoute)
//                navController.navigate(Screen.Result.routeWithParams(uri, projection, selection, selectionArgs, sortOrder))
            }
            )
        }
        composable(route = Screen.Result.route) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")
            requireNotNull(uri) { "URI not found for query"}
            val projection = backStackEntry.arguments?.getString("projection")
            val selection = backStackEntry.arguments?.getString("selection")
            val selectionArgs = backStackEntry.arguments?.getString("selectionArgs")
            val sortOrder = backStackEntry.arguments?.getString("sortOrder")
            // unescaping URI may not actually be necessary, as seems to be done already
            val queryHolder = QueryHolder(unEscapeUriString(uri), projection, selection, selectionArgs, sortOrder)
            ResultScreen(queryHolder)
        }
    }
}

sealed class Screen(val route: String) {
    object Query: Screen("query")
    object Result: Screen("result/{uri}?projection={projection}&selection={selection}&selectionArgs={selectionArgs}&sortOrder={sortOrder}") {
        fun routeWithParams(uri: String,
                  projection: String?,
                  selection: String?,
                  selectionArgs: String?,
                  sortOrder: String?) = "result/${uri}?projection=$projection&selection=$selection&selectionArgs=$selectionArgs&sortOrder=$sortOrder"
    }
}

data class QueryHolder(
    val uri: String,
    val projection: String?,
    val selection: String?,
    val selectionArgs: String?,
    val sortOrder: String?
)