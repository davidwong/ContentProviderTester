package au.com.dw.contentprovidertester.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.query.QueryScreen1
import au.com.dw.contentprovidertester.ui.result.ResultScreen1
import au.com.dw.contentprovidertester.ui.result.ResultScreen2
import au.com.dw.contentprovidertester.ui.result.TestScreen
import au.com.dw.contentprovidertester.ui.unEscapeUriString

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val vm: QueryViewModel = hiltViewModel()
    NavHost(navController, startDestination = Screen.Query.route) {
        composable(route = Screen.Query.route) {
            QueryScreen1(vm, navController = navController)

//            QueryScreen2(onQuery2 =
//            {
//                uri, projection, selection, selectionArgs, sortOrder ->
//                navController.navigate(Screen.Result2.routeWithParams(uri, projection, selection, selectionArgs, sortOrder))
//            }
//            )
        }
        composable(route = Screen.Result1.route) {
            ResultScreen1(vm) {
                vm.reset()
                navController.navigateUp()
            }
        }
        composable(route = Screen.Result2.route) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")
            requireNotNull(uri) { "URI not found for query"}
            val projection = backStackEntry.arguments?.getString("projection")
            val selection = backStackEntry.arguments?.getString("selection")
            val selectionArgs = backStackEntry.arguments?.getString("selectionArgs")
            val sortOrder = backStackEntry.arguments?.getString("sortOrder")
            // unescaping URI may not actually be necessary, as seems to be done already
            val queryHolder = QueryHolder(unEscapeUriString(uri), projection, selection, selectionArgs, sortOrder)
            ResultScreen2(queryHolder)
        }
        composable(Screen.Test.route)
        {
            TestScreen()
        }
    }
}

sealed class Screen(val route: String) {
    object Query: Screen("query")
    object Test: Screen("test")
    object Result1: Screen("result1")
    object Result2: Screen("result2{uri}?projection={projection}&selection={selection}&selectionArgs={selectionArgs}&sortOrder={sortOrder}") {
        fun routeWithParams(uri: String,
                  projection: String?,
                  selection: String?,
                  selectionArgs: String?,
                  sortOrder: String?) = "result2/${uri}?projection=$projection&selection=$selection&selectionArgs=$selectionArgs&sortOrder=$sortOrder"
    }
}

data class QueryHolder(
    val uri: String,
    val projection: String?,
    val selection: String?,
    val selectionArgs: String?,
    val sortOrder: String?
)