package com.example.chefconnect.data.repository

import com.example.chefconnect.data.network.MealApiService

class MealRepository(private val api: MealApiService) {
    suspend fun getCategories() = api.getCategories()
    suspend fun getMealsByCategory(category: String) = api.getMealsByCategory(category)
    suspend fun searchMeals(query: String) = api.searchMeals(query)
    suspend fun getMealDetails(id: String) = api.getMealDetails(id)
}