package au.com.dw.contentprovidertester.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import au.com.dw.contentprovidertester.ui.navigation.AppNavigation
import au.com.dw.contentprovidertester.ui.query.QueryScreen

class MainComposeActivity : ComponentActivity() {
    private lateinit var queryViewModel: QueryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        queryViewModel = ViewModelProvider(this, QueryViewModelFactory())
            .get(QueryViewModel::class.java)


        setContent {
        val queryResult2 = queryViewModel.queryDisplayResult.observeAsState()

            AppNavigation( queryViewModel )
        }
    }
}