package com.example.chefconnect.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chefconnect.data.model.*
import com.example.chefconnect.data.repository.MealRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class MealState {
    object Loading : MealState()
    data class Success(val meals: List<Meal>) : MealState()
    data class Error(val message: String) : MealState()
}

@OptIn(FlowPreview::class)
class MealViewModel(private val repository: MealRepository) : ViewModel() {

    var categoryState by mutableStateOf<List<Category>?>(null)
        private set

    var mealState by mutableStateOf<MealState>(MealState.Loading)
        private set

    var favorites = mutableStateListOf<Meal>()
        private set

    private val searchQuery = MutableStateFlow("")

    init {
        fetchCategories()
        viewModelScope.launch {
            searchQuery
                .debounce(500)
                .filter { it.isNotBlank() }
                .collect { query ->
                    search(query)
                }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try { categoryState = repository.getCategories().categories }
            catch (e: Exception) { /* Manejar error */ }
        }
    }

    fun fetchMealsByCategory(category: String) {
        viewModelScope.launch {
            mealState = MealState.Loading
            try {
                val response = repository.getMealsByCategory(category)
                mealState = MealState.Success(response.meals ?: emptyList())
            } catch (e: Exception) {
                mealState = MealState.Error("Error al cargar platillos")
            }
        }
    }

    fun searchMeals(query: String) {
        searchQuery.value = query
    }

    private suspend fun search(query: String) {
        mealState = MealState.Loading
        try {
            val response = repository.searchMeals(query)
            mealState = MealState.Success(response.meals ?: emptyList())
        } catch (e: Exception) { mealState = MealState.Error("Error de búsqueda") }
    }

    fun toggleFavorite(meal: Meal) {
        if (favorites.contains(meal)) favorites.remove(meal) else favorites.add(meal)
    }
}