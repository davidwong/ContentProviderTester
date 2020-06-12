package au.com.dw.contentprovidertester.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import au.com.dw.contentprovidertester.R
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import java.io.Serializable

/**
 * Get the ContentResolver query parameters from the user and do the query.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var queryViewModel: QueryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uri = findViewById<EditText>(R.id.uri)
        val projection = findViewById<EditText>(R.id.projection)
        val selection = findViewById<EditText>(R.id.selection)
        val selection_args = findViewById<EditText>(R.id.selection_args)
        val sort_order = findViewById<EditText>(R.id.sort_order)
        val process_query = findViewById<Button>(R.id.query)
        val loading = findViewById<ProgressBar>(R.id.loading)

        queryViewModel = ViewModelProvider(this, QueryViewModelFactory())
            .get(QueryViewModel::class.java)

        queryViewModel.queryDisplayResult.observe(this@MainActivity, Observer {
            val queryResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (queryResult.error != null) {
                showQueryFailed(queryResult.error)
            }
            if (queryResult.success != null) {
                 val intent = Intent(this, ResultActivity::class.java)
                // unfortunately difficult to make QueryResult parcelable due to List<Map>> type in results
                intent.putExtra(PARAM_KEY, queryResult.success.params)
                intent.putExtra(RESULT_KEY, queryResult.success.results as Serializable)
                intent.putExtra(TIME_KEY, queryResult.success.executionTime)
                startActivity(intent)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy query activity once successful
            finish()
        })

        process_query.setOnClickListener {
            loading.visibility = View.VISIBLE
            methodRequiresPermissions(applicationContext,
                uri.text.toString(),
                projection.text.toString(),
                selection.text.toString(),
                selection_args.text.toString(),
                sort_order.text.toString()
            )
        }
    }

    private fun methodRequiresPermissions(context: Context, uri: String, projection: String,
                selection: String, selectionArgs: String, sortOrder: String) = runWithPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS) {
        Toast.makeText(this, "Cal and microphone permissions granted", Toast.LENGTH_LONG).show()
        queryViewModel.processQuery(applicationContext, uri,
            projection,
            selection,
            selectionArgs,
            sortOrder)
    }
    private fun showQueryFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}
