package com.example.chefconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.navDeepLink
import com.example.chefconnect.data.network.RetrofitClient
import com.example.chefconnect.data.repository.MealRepository
import com.example.chefconnect.ui.screens.*
import com.example.chefconnect.ui.theme.ChefConnectTheme
import com.example.chefconnect.ui.viewmodel.MealViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos las dependencias
        val repository = MealRepository(RetrofitClient.instance)
        val viewModel = MealViewModel(repository)

        setContent {
            ChefConnectTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(selected = true, onClick = { navController.navigate("home") }, icon = { Text("🏠") }, label = { Text("Home") })
                                NavigationBarItem(selected = false, onClick = { navController.navigate("search") }, icon = { Text("🔍") }, label = { Text("Search") })
                            }
                        }
                    ) { padding ->
                        NavHost(navController, startDestination = "home", Modifier.padding(padding)) {
                            composable("home") { CategoriesScreen(viewModel, navController) }
                            composable("grid/{cat}") { backStackEntry ->
                                val cat = backStackEntry.arguments?.getString("cat") ?: ""
                                MealGridScreen(cat, viewModel, navController)
                            }
                            composable("search") { SearchScreen(viewModel, navController) }

                            // Deep Link configurado para abrir el detalle[cite: 1]
                            composable(
                                route = "details/{mealId}",
                                deepLinks = listOf(navDeepLink { uriPattern = "chefconnect://details/{mealId}" })
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("mealId") ?: ""
                                Text("Detalle del plato con ID: $id", Modifier.fillMaxSize().padding(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}