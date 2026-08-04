package com.example.fred_analysis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fred_analysis.ui.screens.LaunchScreen
import com.example.fred_analysis.ui.theme.FINAL_FRED_DISPLAYTheme
import com.example.fred_analysis.viewmodel.StartupViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FINAL_FRED_DISPLAYTheme(dynamicColor = false) {
                val startupViewModel: StartupViewModel = hiltViewModel()
                val isReady by startupViewModel.isReady.collectAsStateWithLifecycle()

                if (isReady) NavWrapper() else LaunchScreen()
            }
        }
    }
}
