package com.example.chefconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import androidx.navigation.navDeepLink
import com.example.chefconnect.data.network.RetrofitClient
import com.example.chefconnect.data.repository.MealRepository
import com.example.chefconnect.ui.screens.*
import com.example.chefconnect.ui.theme.*
import com.example.chefconnect.ui.viewmodel.MealViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = MealRepository(RetrofitClient.instance)
        val viewModel = MealViewModel(repository)

        setContent {
            // ¡Aplicamos nuestro nuevo tema verde!
            ChefConnectTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundColor) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    Scaffold(
                        bottomBar = {
                            // Ocultar BottomBar en la pantalla de detalles
                            if (currentRoute?.startsWith("details") != true) {
                                NavigationBar(
                                    containerColor = Color.White,
                                    contentColor = DarkGreen,
                                    tonalElevation = 8.dp
                                ) {
                                    NavigationBarItem(
                                        selected = currentRoute == "home",
                                        onClick = { navController.navigate("home") },
                                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                        label = { Text("Home") },
                                        colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGreen, indicatorColor = LightGreen)
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "search",
                                        onClick = { navController.navigate("search") },
                                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                        label = { Text("Search") },
                                        colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGreen, indicatorColor = LightGreen)
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "favorites",
                                        onClick = { navController.navigate("favorites") },
                                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                                        label = { Text("Favs") },
                                        colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryGreen, indicatorColor = LightGreen)
                                    )
                                }
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

                            composable("favorites") {
                                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("My Saved Recipes", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = DarkGreen)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                                        items(viewModel.favorites) { meal ->
                                            RecipeCard(
                                                title = meal.strMeal,
                                                imageUrl = meal.strMealThumb,
                                                onClick = { navController.navigate("details/${meal.idMeal}") }
                                            )
                                        }
                                    }
                                }
                            }

                            // Nueva pantalla de Detalles conectada
                            composable(
                                route = "details/{mealId}",
                                deepLinks = listOf(navDeepLink { uriPattern = "chefconnect://details/{mealId}" })
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("mealId") ?: ""
                                DetailScreen(id, viewModel, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}