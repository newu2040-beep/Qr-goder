package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun QRGeneratorApp(factory: ViewModelProvider.Factory) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel(factory = factory)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = com.example.ui.theme.BentoSecondary,
                contentColor = com.example.ui.theme.TextCol,
                tonalElevation = 0.dp,
                modifier = Modifier.drawBehind { drawLine(com.example.ui.theme.BentoSecondaryBorder, Offset(0f, 0f), Offset(size.width, 0f), 2f) }
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { Icon(Icons.Default.QrCode, contentDescription = "Generate") },
                    label = { Text("Create", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold) },
                    selected = currentRoute == "generate",
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.BentoDarkPurple,
                        selectedTextColor = com.example.ui.theme.BentoDarkPurple,
                        indicatorColor = com.example.ui.theme.BentoLightPurple
                    ),
                    onClick = {
                        navController.navigate("generate") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Saved") },
                    label = { Text("Studio", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold) },
                    selected = currentRoute == "saved",
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.BentoDarkPurple,
                        selectedTextColor = com.example.ui.theme.BentoDarkPurple,
                        indicatorColor = com.example.ui.theme.BentoLightPurple
                    ),
                    onClick = {
                        navController.navigate("saved") {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "generate",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("generate") {
                GenerateScreen(viewModel)
            }
            composable("saved") {
                SavedScreen(viewModel)
            }
        }
    }
}
