package com.example.fred_analysis


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fred_analysis.ui.screens.GraphScreen
import com.example.fred_analysis.ui.screens.HomeScreen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


@Composable
fun NavWrapper() {
    val navController = rememberNavController()

    val screens = listOf(NavItem("Home", "home", Icons.Filled.Home), NavItem("Graph", "graph", Icons.AutoMirrored.Filled.ShowChart))
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(modifier = Modifier.fillMaxWidth()
                .statusBarsPadding())
            {
                Text(modifier = Modifier.fillMaxWidth(),
                    text = "FRED Data Display",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center, fontSize = 40.sp
                    )
            }
        },
        bottomBar = {
        NavigationBar()
        {
            screens.forEach { screen ->
                NavigationBarItem(
                    selected = navController.currentDestination?.route==screen.route,
                    onClick = { navController.navigate(screen.route) },
                    label = { Text(screen.label) },
                    icon = { Icon(
                        screen.imageVector, contentDescription = screen.label)}
                )
            }
        }
    })
    {
        innerPadding -> Box(modifier = Modifier.padding(innerPadding))
    {
        NavHost(
            navController = navController,
            startDestination = "home"
        )
        {
            composable("home")
            {
                HomeScreen(navController = navController)
            }
            composable(route ="graph/{seriesId}/{startDate}/{endDate}/{counter}")
            {
                GraphScreen(navController = navController)
            }
        }
    }
    }


}
data class NavItem(val label : String, val route : String, val imageVector : ImageVector)