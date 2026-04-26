package com.kaushalya.karnataka

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.kaushalya.karnataka.core.ui.theme.KaushalyaTheme
import com.kaushalya.karnataka.presentation.nav.AppNavGraph
import com.kaushalya.karnataka.presentation.nav.AppRootViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KaushalyaTheme {
                Surface(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val rootViewModel: AppRootViewModel = hiltViewModel()
                    AppNavGraph(
                        navController = navController,
                        startDestination = rootViewModel.startDestination
                    )
                }
            }
        }
    }
}
