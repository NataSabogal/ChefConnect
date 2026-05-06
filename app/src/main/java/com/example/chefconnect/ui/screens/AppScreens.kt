package com.example.chefconnect.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.chefconnect.ui.viewmodel.*

@Composable
fun CategoriesScreen(viewModel: MealViewModel, navController: NavController) {
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val categories = viewModel.categoryState
    if (categories == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
            items(categories) { cat ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { navController.navigate("grid/${cat.strCategory}") }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(model = cat.strCategoryThumb, contentDescription = null, modifier = Modifier.height(120.dp))
                        Text(cat.strCategory, Modifier.padding(8.dp), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun MealGridScreen(category: String, viewModel: MealViewModel, navController: NavController) {
    LaunchedEffect(category) { viewModel.fetchMealsByCategory(category) }

    when (val state = viewModel.mealState) {
        is MealState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is MealState.Error -> Text(state.message, Modifier.padding(16.dp))
        is MealState.Success -> {
            LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(8.dp)) {
                items(state.meals) { meal ->
                    Card(modifier = Modifier
                        .padding(8.dp)
                        .clickable { navController.navigate("details/${meal.idMeal}") }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(model = meal.strMealThumb, contentDescription = null, modifier = Modifier.height(120.dp))
                            Text(meal.strMeal, Modifier.padding(8.dp), maxLines = 1)
                            Button(onClick = { viewModel.toggleFavorite(meal) }, modifier = Modifier.padding(bottom = 8.dp)) {
                                Text(if (viewModel.favorites.contains(meal)) "Quitar Fav" else "Favorito")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: MealViewModel, navController: NavController) {
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it; viewModel.searchMeals(it) },
            label = { Text("Buscar receta...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        when (val state = viewModel.mealState) {
            is MealState.Success -> LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(state.meals) { meal ->
                    Card(modifier = Modifier.padding(8.dp).clickable { navController.navigate("details/${meal.idMeal}") }) {
                        Column {
                            AsyncImage(model = meal.strMealThumb, contentDescription = null, modifier = Modifier.height(100.dp))
                            Text(meal.strMeal, Modifier.padding(8.dp), maxLines = 1)
                        }
                    }
                }
            }
            else -> {}
        }
    }
}