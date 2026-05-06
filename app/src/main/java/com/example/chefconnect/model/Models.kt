package com.example.chefconnect.data.model

data class CategoryResponse(val categories: List<Category>)
data class Category(val strCategory: String, val strCategoryThumb: String)

data class MealResponse(val meals: List<Meal>?)
data class Meal(val idMeal: String, val strMeal: String, val strMealThumb: String)