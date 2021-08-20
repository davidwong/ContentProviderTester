package au.com.dw.contentprovidertester.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import au.com.dw.contentprovidertester.ui.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    private lateinit var queryViewModel: QueryViewModel
    private val queryViewModel: QueryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        queryViewModel = ViewModelProvider(this, QueryViewModelFactory())
//            .get(QueryViewModel::class.java)


        setContent {
        val queryResult2 = queryViewModel.queryUiState.observeAsState()

            AppNavigation( queryViewModel )
        }
    }
}