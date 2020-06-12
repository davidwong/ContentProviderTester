package au.com.dw.contentprovidertester.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import au.com.dw.contentprovidertester.R
import au.com.dw.contentprovidertester.query.executionTimeDisplay
import au.com.dw.contentprovidertester.query.model.QueryParam
import au.com.dw.contentprovidertester.ui.RecyclerAdapter.RowViewHolder
import au.com.dw.contentprovidertester.ui.login.LoginViewModel
import au.com.dw.contentprovidertester.ui.login.LoginViewModelFactory

class ResultActivity : AppCompatActivity() {

    private lateinit var adapter: RecyclerAdapter
    private lateinit var recyclerView: RecyclerView

    val newLine = "\n"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val queryDetails = findViewById<TextView>(R.id.query_details)
        recyclerView = findViewById(R.id.result_list)
        adapter = RecyclerAdapter()

        val extras = intent.extras

        val results = extras?.get(RESULT_KEY) as List<Map<String, Any>>
        if (results != null) {
            adapter.results = results
        }
        val params = extras?.get(PARAM_KEY)
        if (params != null)
        {
            var paramText = getParamString(params as QueryParam)
            // add metadata
            paramText += "result count = " + results?.count() + newLine

            val time = extras?.getLong(TIME_KEY, 0L)
            paramText += "execution time = " + executionTimeDisplay(time)

            queryDetails.text = paramText
        }

        recyclerView.adapter = adapter
    }

    private fun getParamString(params: QueryParam): String
    {

        val builder = StringBuilder()
        builder.append("Results of ContentResolver Query" + newLine)
            .append("uri = " + params.uri + newLine)
            .append("projection = " + params.projection.toString() + newLine)
            .append("selection = " + params.selection + newLine)
            .append("selectionArgs = " + params.selectionArgs.toString() + newLine)
            .append("sortOrder = " + params.sortOrder + newLine)
        return  builder.toString()
    }
}

class RecyclerAdapter : RecyclerView.Adapter<RowViewHolder>()  {
    var results: List<Map<String, Any>> = ArrayList<Map<String, Any>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.list_content, parent, false)
        return RowViewHolder(view)
    }

    override fun getItemCount(): Int {
        return results.size
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder?.bind(results[position], position)
    }

    class RowViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView) {
        private val rowTextView: TextView = rowView.findViewById<TextView>(R.id.row)

        fun bind(row: Map<String, Any>, position: Int) {
            var rowDisplay: String = "Row " + (position + 1) + ": "
            row.forEach {
                rowDisplay += it.key + "=" + it.value + ", "
            }
            rowTextView.text = rowDisplay
        }
    }
}


