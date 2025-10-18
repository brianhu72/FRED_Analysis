package com.example.final_fred_display

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.final_fred_display.ui.screens.GraphScreen
import com.example.final_fred_display.ui.screens.HomeScreen

@Composable
fun NavWrapper() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    )
    {
        composable("home")
        {
            HomeScreen(navHostController = navController)
        }
        composable(route ="graph/{seriesId}/{startDate}/{endDate}")
        {
            GraphScreen(navHostController = navController)
        }
    }

}