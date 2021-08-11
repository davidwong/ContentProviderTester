package au.com.dw.contentprovidertester.ui.query

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.Snapshot.Companion.observe
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.textInputServiceFactory
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import au.com.dw.contentprovidertester.data.Result
import au.com.dw.contentprovidertester.data.model.QueryResult
import au.com.dw.contentprovidertester.ui.QueryDisplayResult
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.navigation.Screen

@Composable
fun QueryScreen(vm: QueryViewModel, navController: NavController)
{
    val queryResult = vm.queryDisplayResult.observeAsState()
    val name: String by vm.name.observeAsState("")

    if (queryResult.value is QueryDisplayResult.Success<*>)
//    if (name.equals("done"))
    {
        navController.navigate(Screen.Result.route)
    }
    else
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
            BodyContent(Modifier.padding(innerPadding), vm::onNameChange, vm::processQuery, navController)
        }
    }
}


@Composable
fun BodyContent(modifier: Modifier = Modifier, onNameChange: (String) -> Unit, sendQuery: (Context, String, String, String, String, String) -> Unit, navController: NavController)
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
//                onClick = { navController.navigate(Screen.Result.route) }

                onClick = { sendQuery(context, uri, projection, selection, selectionArgs, sortOrder) }
//                onClick = { onNameChange("done") }
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