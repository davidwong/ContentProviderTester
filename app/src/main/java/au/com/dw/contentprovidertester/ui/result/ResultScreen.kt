package au.com.dw.contentprovidertester.ui.result

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel

import au.com.dw.contentprovidertester.data.model.QueryResult
import au.com.dw.contentprovidertester.ui.QueryUiState
import au.com.dw.contentprovidertester.ui.QueryViewModel
import au.com.dw.contentprovidertester.ui.navigation.QueryHolder
import au.com.dw.contentprovidertester.ui.tableview.*
import com.evrencoskun.tableview.CellAllowClick
import com.evrencoskun.tableview.MyTableView

@Composable
fun TestScreen(vm: QueryViewModel = hiltViewModel())
{
    vm.reset()
    Text("hello6")
}

@Composable
fun ResultScreen1(vm: QueryViewModel, onBack: () -> Unit)
{
    val uiState = vm.queryUiState.observeAsState()

    if (uiState.value is QueryUiState.Success<*>) {
        val result = (uiState.value as QueryUiState.Success<*>).data as QueryResult
        TableScreen(result, onBack)
    }
    else
    {
        // this shouldn't happen as successful query should have already been checked
        Text("Unable to display results")
    }
}

@Composable
fun ResultScreen2(queryHolder: QueryHolder, vm: QueryViewModel = hiltViewModel())
{
    val context = LocalContext.current
    vm.processQuery(context, queryHolder.uri, queryHolder.projection, queryHolder.selection, queryHolder.selectionArgs, queryHolder.sortOrder)
    /**
     * The UI state determines what to display
     * - on successful query, pass to result screen to show results
     * - if there has been an error or failure (no results for the query), then show an additional
     * snackbar message
     */
    vm.queryUiState.observeAsState().value?.let { uiState ->
        when (uiState) {
            is QueryUiState.Loading -> showProgressIndicator()
            is QueryUiState.Success<*> -> TableScreen((uiState as QueryUiState.Success<*>).data as QueryResult, null)
            is QueryUiState.Error -> {
                val error = uiState as QueryUiState.Error
                ShowError(errorMsg = "Error in query: " + error.exception.message, logMessage = "Query error", logThrowable = error.exception)
            }
            is QueryUiState.Failure -> {
                val failure = uiState as QueryUiState.Failure
                ShowError(errorMsg = failure.message, logMessage = "Query failure: " + failure.message, logThrowable = null)
            }
        }
    }
}

@Composable
fun TableScreen(queryResult: QueryResult, onBack: (() -> Unit)?) {
        onBack?.let { BackHandler(onBack = onBack) }
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
            val tableListsHolder = collateTableData(queryResult.results)
            TableContent(Modifier.padding(innerPadding), queryResult, tableListsHolder)
        }
}

@Composable
fun TableContent(modifier: Modifier = Modifier, queryResult: QueryResult, listsHolder : TableListsHolder) {

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

@Composable
fun showProgressIndicator()
{
    Box() {
        CircularProgressIndicator()
    }
}

@Composable
fun ShowError(errorMsg: String, logMessage: String?, logThrowable: Throwable?)
{
    if (null == logThrowable)
    {
        Log.e("QueryScreen", logMessage!!)
    }
    else {
        Log.e("QueryScreen", logMessage, logThrowable)
    }

    Text(text = errorMsg)
}
//@Preview
//@Composable
//fun PreviewResultScreen() {
//    ResultScreen()
//}