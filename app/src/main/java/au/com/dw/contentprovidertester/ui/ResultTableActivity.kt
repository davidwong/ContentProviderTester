package au.com.dw.contentprovidertester.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import au.com.dw.contentprovidertester.R
import au.com.dw.contentprovidertester.query.executionTimeDisplay
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.ui.RecyclerAdapter.RowViewHolder
import au.com.dw.contentprovidertester.ui.tableview.*
import com.evrencoskun.tableview.TableView

/**
 * Display the results of the ContentResolver query, including the parameters used in the query
 * and some metadata.
 */
class ResultTableActivity : AppCompatActivity() {

    private lateinit var adapter: RecyclerAdapter
    private lateinit var recyclerView: RecyclerView

    val newLine = "\n"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val tableView = TableView(this)
//        val adapter = TableViewAdapter()
//        tableView.setAdapter(adapter)
//
//        // items MUST be added after TableView.setAdapter(), which also sets the tableview field
//        // in the adapter to itself, else will get a NullPointerException
//        // since setAllItems needs access to the TableView for layout sizing
//        val cells1 = listOf<Cell>(Cell("1", "hello"), Cell("2", "world"))
//        val cells2 = listOf<Cell>(Cell("3", "next"), Cell("4", "line"))
//        adapter.setAllItems(listOf(ColumnHeader("col1"), ColumnHeader("col2")),
//            listOf(RowHeader("1"), RowHeader("2")),
//            listOf(cells1, cells2))
//
//        tableView.tableViewListener = TableViewListener(tableView)
//
//        setContentView(tableView)
    }

}


