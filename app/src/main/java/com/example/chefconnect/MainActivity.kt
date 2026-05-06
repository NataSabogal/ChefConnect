package com.example.chefconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.navDeepLink
import com.example.chefconnect.data.network.RetrofitClient
import com.example.chefconnect.data.repository.MealRepository
import com.example.chefconnect.ui.screens.*
import com.example.chefconnect.ui.viewmodel.MealViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos las dependencias
        val repository = MealRepository(RetrofitClient.instance)
        val viewModel = MealViewModel(repository)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(selected = false, onClick = { navController.navigate("home") }, icon = { Text("🏠") }, label = { Text("Home") })
                                NavigationBarItem(selected = false, onClick = { navController.navigate("search") }, icon = { Text("🔍") }, label = { Text("Search") })
                                NavigationBarItem(selected = false, onClick = { navController.navigate("favorites") }, icon = { Text("⭐") }, label = { Text("Favs") })
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

                            // Pantalla de Favoritos
                            // Pantalla de Favoritos (¡Ahora con imagen!)
                            composable("favorites") {
                                LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
                                    items(viewModel.favorites) { meal ->
                                        Card(modifier = Modifier.padding(8.dp).clickable { navController.navigate("details/${meal.idMeal}") }) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                // ¡Aquí agregamos la imagen que faltaba!
                                                androidx.compose.foundation.Image(
                                                    painter = coil.compose.rememberAsyncImagePainter(
                                                        model = meal.strMealThumb,
                                                        placeholder = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_gallery)
                                                    ),
                                                    contentDescription = null,
                                                    modifier = Modifier.height(120.dp)
                                                )
                                                Text(meal.strMeal, Modifier.padding(8.dp), maxLines = 1)
                                            }
                                        }
                                    }
                                }
                            }

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