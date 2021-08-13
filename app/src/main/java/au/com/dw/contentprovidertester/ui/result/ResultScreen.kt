package au.com.dw.contentprovidertester.ui.result

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
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
            // get the results to pass to the table view
            val result = (uiState.value as QueryUiState.Success<*>).data as QueryResult
            val tableListsHolder = collateTableData(result.results)
            TableScreen(Modifier.padding(innerPadding), tableListsHolder)
        }
    }
    else
    {
        // this shouldn't happen as successful query should have already been checked
        Text("Unable to display results")
    }
}

@Composable
fun TableScreen(modifier: Modifier = Modifier, listsHolder : TableListsHolder) {

    // Show the query results in a table view, as data tables are not availabe in the compose library
    AndroidView(
        modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
        factory = { context ->
            val tableView = MyTableView(context, CellAllowClick(false, true, true))
            val adapter = TableViewAdapter()
            tableView.setAdapter(adapter)

            // items MUST be added after TableView.setAdapter(), which also sets the tableview field
            // in the adapter to itself, else will get a NullPointerException
            // since setAllItems needs access to the TableView for layout sizing

            adapter.setAllItems(listsHolder.columnHeaders, listsHolder.rowHeaders, listsHolder.cells)

            tableView.tableViewListener = TableViewListener(tableView)
            tableView
        },
    )
}

/**
 * Convert the query results into the format required for the table view.
 */
fun collateTableData(results : List<Map<String, Any>>) : TableListsHolder
{
    var columnHeaderList = getColumnHeaderList(results)
    var rowHeaderList = getRowHeaderList(results)

    var cellListOfLists : MutableList<MutableList<Cell>> = mutableListOf()

    results.forEachIndexed { index, map ->
        var cellList : MutableList<Cell> = mutableListOf()
        columnHeaderList.forEachIndexed { colIndex, col ->
            // the id for the cell is a combination of the column index and the row index
            cellList.add(Cell(colIndex.toString() + "-" + index, map.get(col.data)))
        }
        cellListOfLists.add(cellList)
    }

    return TableListsHolder(columnHeaderList.toList(),  rowHeaderList.toList(), cellListOfLists)
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