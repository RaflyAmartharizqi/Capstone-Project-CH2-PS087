package com.bangkitacademy.nutrace.data.model

data class Food(
    val calories: Double,
    val carbohydrateContent: Double,
    val cookTime: String,
    val fatContent: Double,
    val images: String,
    val prepTime: String,
    val proteinContent: Double,
    val recipeIngredientParts: List<String>,
    val recipeInstructions: List<String>,
    val recipeServings: Int,
    val totalTime: String,
    val foodName: String,
)