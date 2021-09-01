package au.com.dw.contentprovidertester.ui.result

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import au.com.dw.contentprovidertester.data.model.QueryResult
import au.com.dw.contentprovidertester.ui.tableview.*
import com.evrencoskun.tableview.CellAllowClick
import com.evrencoskun.tableview.MyTableView

@Composable
fun TableScreen(queryResult: QueryResult, onBack: () -> Unit) {
    onBack?.let { BackHandler(onBack = onBack) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Query Results")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
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