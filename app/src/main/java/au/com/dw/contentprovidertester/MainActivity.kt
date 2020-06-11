package au.com.dw.contentprovidertester

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import au.com.dw.contentprovidertester.ui.login.LoginViewModel
import au.com.dw.contentprovidertester.ui.login.LoginViewModelFactory
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions

class MainActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val selection = findViewById<EditText>(R.id.selection)
        val selection_args = findViewById<EditText>(R.id.selection_args)
        val sort_order = findViewById<EditText>(R.id.sort_order)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)


        loginViewModel.loginResult.observe(this@MainActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                // todo show results
                Toast.makeText(applicationContext, loginResult.success.results.count().toString(), Toast.LENGTH_SHORT).show()
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            methodRequiresPermissions(applicationContext,
                username.text.toString(),
                password.text.toString(),
                selection.text.toString(),
                selection_args.text.toString(),
                sort_order.text.toString()
            )
        }
    }

    private fun methodRequiresPermissions(context: Context, uri: String, projection: String,
                selection: String, selectionArgs: String, sortOrder: String) = runWithPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS) {
        Toast.makeText(this, "Cal and microphone permissions granted", Toast.LENGTH_LONG).show()
        loginViewModel.login(applicationContext, uri,
            projection,
            selection,
            selectionArgs,
            sortOrder)
    }
    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}
