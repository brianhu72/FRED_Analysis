package com.example.fred_analysis

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fred_analysis.ui.screens.GraphScreen
import com.example.fred_analysis.ui.screens.HomeScreen
import java.time.LocalDate

private const val HOME_ROUTE = "home"
private const val GRAPH_ROUTE = "graph/{seriesIds}/{startDate}/{endDate}"

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NavWrapper() {
    val navController = rememberNavController()
    val screens = listOf(
        NavItem("Explore", HOME_ROUTE, Icons.Filled.Home),
        NavItem("Chart", "graph", Icons.AutoMirrored.Filled.ShowChart)
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val navigationColor = MaterialTheme.colorScheme.surfaceContainer

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.foundation.layout.Column {
                        Text("FRED Analysis", fontWeight = FontWeight.SemiBold)
                        Text("Visualizing economic data", style = MaterialTheme.typography.labelMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = navigationColor)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = navigationColor) {
                screens.forEach { screen ->
                    val currentRoute = backStackEntry?.destination?.route.orEmpty()
                    val selected = if (screen.route == "graph") currentRoute.startsWith("graph")
                    else backStackEntry?.destination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (screen.route == HOME_ROUTE) {
                                navController.navigate(HOME_ROUTE) { popUpTo(HOME_ROUTE) { inclusive = false } }
                            } else if (!selected) {
                                navController.navigate("graph/GDP/2019-01-01/${LocalDate.now()}") {
                                    launchSingleTop = true
                                }
                            }
                        },
                        icon = { Icon(screen.imageVector, contentDescription = null) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = HOME_ROUTE) {
                composable(HOME_ROUTE) {
                    HomeScreen(onViewChart = { seriesIds, startDate, endDate ->
                        navController.navigate("graph/${seriesIds.joinToString(",")}/$startDate/$endDate")
                    })
                }
                composable(GRAPH_ROUTE) { GraphScreen(onBack = { navController.popBackStack() }) }
            }
        }
    }
}

private data class NavItem(val label: String, val route: String, val imageVector: ImageVector)
