package au.com.dw.contentprovidertester.ui.result

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import au.com.dw.contentprovidertester.data.model.QueryResult
import au.com.dw.contentprovidertester.ui.QueryUiState
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.tableview.*
import com.evrencoskun.tableview.CellAllowClick
import com.evrencoskun.tableview.MyTableView

@Composable
fun ResultScreen(vm: QueryViewModel)
{
    val uiState = vm.queryUiState.observeAsState()

    if (uiState.value is QueryUiState.Success<*>) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Query Results")
                    }
                )
            }
        ) { innerPadding ->
//            val tableListsHolder = collateTableData(uiState.value.data)
            TableScreen(Modifier.padding(innerPadding))
        }
    }
    else
    {
        // this shouldn't happen as successful query should have already been checked
        Text("Unable to display results")
    }
}

@Composable
//fun TableScreen(modifier: Modifier = Modifier, listsHolder : TableListsHolder) {
fun TableScreen(modifier: Modifier = Modifier) {

    // Adds view to Compose
    AndroidView(
        modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
        factory = { context ->
            val tableView = MyTableView(context, CellAllowClick(false, true, true))
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

fun collateTableData(data : List<Map<String, Any>>) : TableListsHolder
{
    var columnHeaderList = getColumnHeaderList(data)
    var rowHeaderList = getRowHeaderList(data)

    var cellList : MutableList<Cell> = mutableListOf()

    data.forEachIndexed { index, map ->
        columnHeaderList.forEachIndexed { colIndex, col ->
            // the id for the cell is a combination of the column index and the row index
            cellList.add(Cell(colIndex.toString() + "-" + index, map.get(col.data)))
        }
    }

    return TableListsHolder(columnHeaderList.toList(),  rowHeaderList.toList(), cellList.toList())
}

fun getColumnHeaderList(data : List<Map<String, Any>>) : List<ColumnHeader>
{
    var columnHeaderList : MutableList<ColumnHeader> = mutableListOf()

    val map = data[0]

    map.keys.forEachIndexed { index, s ->
        columnHeaderList.add(ColumnHeader(index.toString(), s))
    }

    return columnHeaderList.toList()
}

fun getRowHeaderList(data : List<Map<String, Any>>) : List<RowHeader>
{
    var rowHeaderList : MutableList<RowHeader> = mutableListOf()

    data.forEachIndexed { index, map ->
        val rowNumber = index + 1
        rowHeaderList.add(RowHeader(index.toString(), rowNumber.toString()))
    }

    return rowHeaderList.toList()
}

fun displayBlank(value: String, bookend: String): String = bookend + value + bookend

//@Preview
//@Composable
//fun PreviewResultScreen() {
//    ResultScreen()
//}