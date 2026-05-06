package com.example.chefconnect.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.chefconnect.ui.theme.*
import com.example.chefconnect.ui.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(viewModel: MealViewModel, navController: NavController) {
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val categories = viewModel.categoryState

    Column(modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "What are we cooking\ntoday?",
            color = DarkGreen,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar (Visual)
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search recipes, categories...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LightGreen,
                unfocusedContainerColor = LightGreen,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            enabled = false,
            readOnly = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (categories == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryGreen) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(categories) { cat ->
                    RecipeCard(
                        title = cat.strCategory,
                        imageUrl = cat.strCategoryThumb,
                        onClick = { navController.navigate("grid/${cat.strCategory}") }
                    )
                }
            }
        }
    }
}

@Composable
fun MealGridScreen(category: String, viewModel: MealViewModel, navController: NavController) {
    LaunchedEffect(category) { viewModel.fetchMealsByCategory(category) }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "$category Recipes", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = viewModel.mealState) {
            is MealState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryGreen) }
            is MealState.Error -> Text(state.message, color = Color.Red)
            is MealState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(state.meals) { meal ->
                        RecipeCard(
                            title = meal.strMeal,
                            imageUrl = meal.strMealThumb,
                            onClick = { navController.navigate("details/${meal.idMeal}") }
                        )
                    }
                }
            }
            else -> {}
        }
    }
}

// Tarjeta reutilizable con el diseño del Mockup
@Composable
fun RecipeCard(title: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.background(LightGreen, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("Healthy", color = DarkGreen, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("⏱ 20 min", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DetailScreen(mealId: String, viewModel: MealViewModel, navController: NavController) {
    LaunchedEffect(mealId) { viewModel.fetchMealDetail(mealId) }

    when (val state = viewModel.mealState) {
        is MealState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryGreen) }
        is MealState.DetailSuccess -> {
            val meal = state.meal
            val isFav = viewModel.favorites.any { it.idMeal == meal.idMeal }

            Box(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
                // Imagen Hero arriba
                AsyncImage(
                    model = meal.strMealThumb,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(350.dp)
                )

                // Botones flotantes (Atrás y Favorito)
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.background(Color.White, CircleShape)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkGreen)
                    }
                    IconButton(onClick = { viewModel.toggleFavorite(meal) }, modifier = Modifier.background(Color.White, CircleShape)) {
                        Icon(if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "Fav", tint = PrimaryGreen)
                    }
                }

                // Contenedor blanco con bordes redondeados superpuesto
                Surface(
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = SurfaceColor,
                    modifier = Modifier.fillMaxSize().padding(top = 300.dp)
                ) {
                    LazyColumn(modifier = Modifier.padding(24.dp)) {
                        item {
                            Text(text = meal.strMeal, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Chip("⏱ 30 min")
                                Chip("🔥 420 kcal")
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Instructions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = meal.strInstructions ?: "No instructions available.", color = TextDark, lineHeight = 22.sp)
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }

                // Botón "Start Cooking" fijo abajo
                Button(
                    onClick = { /* Acción dummy */ },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp).height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Start Cooking", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        else -> {}
    }
}

@Composable
fun Chip(text: String) {
    Box(modifier = Modifier.background(LightGreen, RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(text, color = DarkGreen, fontWeight = FontWeight.Medium)
    }
}

// Búsqueda (Sin cambios estructurales profundos, pero usando los colores)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MealViewModel, navController: NavController) {
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().background(BackgroundColor).padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = text,
            onValueChange = { text = it; viewModel.searchMeals(it) },
            placeholder = { Text("Search specific recipe...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LightGreen,
                unfocusedContainerColor = LightGreen,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        when (val state = viewModel.mealState) {
            is MealState.Success -> LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(state.meals) { meal ->
                    RecipeCard(title = meal.strMeal, imageUrl = meal.strMealThumb, onClick = { navController.navigate("details/${meal.idMeal}") })
                }
            }
            else -> {}
        }
    }
}