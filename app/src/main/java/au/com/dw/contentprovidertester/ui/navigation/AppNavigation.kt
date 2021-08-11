package au.com.dw.contentprovidertester.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.query.QueryScreen
import au.com.dw.contentprovidertester.ui.result.ResultScreen

@Composable
fun AppNavigation(vm: QueryViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Query.route) {
        composable(route = Screen.Query.route) {
            QueryScreen( vm, navController)
        }
        composable(route = Screen.Result.route) {
            ResultScreen()
        }
    }
}

sealed class Screen(val route: String) {
    object Query: Screen("query")
    object Result: Screen("result")
}