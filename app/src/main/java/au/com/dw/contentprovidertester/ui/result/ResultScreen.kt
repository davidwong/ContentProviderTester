package au.com.dw.contentprovidertester.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.textInputServiceFactory
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import au.com.dw.contentprovidertester.ui.tableview.*
import com.evrencoskun.tableview.CellAllowClick
import com.evrencoskun.tableview.TableView

@Composable
fun ResultScreen()
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
        BodyContent(Modifier.padding(innerPadding))
    }
}


@Composable
fun BodyContent(modifier: Modifier = Modifier)
{
    TableScreen()
}

@Composable
fun TableScreen() {

    // Adds view to Compose
    AndroidView(
        modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
        factory = { context ->
            val tableView = TableView(context, CellAllowClick(false, true, true))
            val adapter = TableViewAdapter()
            tableView.setAdapter(adapter)

            // items MUST be added after TableView.setAdapter(), which also sets the tableview field
            // in the adapter to itself, else will get a NullPointerException
            // since setAllItems needs access to the TableView for layout sizing
            val cells1 = listOf<Cell>(Cell("1-0", "hello"), Cell("2-0", "world"))
            val cells2 = listOf<Cell>(Cell("1-1", "next"), Cell("2-1", "line"))
            adapter.setAllItems(listOf(ColumnHeader("1","col1"), ColumnHeader("2","col2")),
                listOf(RowHeader("1","1"), RowHeader("2","2")),
                listOf(cells1, cells2))

            tableView.tableViewListener = TableViewListener(tableView)
            tableView
        },
    )
}

@Preview
@Composable
fun PreviewResultScreen() {
    ResultScreen()
}