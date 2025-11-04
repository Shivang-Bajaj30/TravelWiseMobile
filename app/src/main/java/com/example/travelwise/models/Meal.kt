package com.example.travelwise.models

data class Meal(
    val id: Int = 0,
    val name: String = "",
    val restaurant: String = "",
    val cuisine: String = "",
    val price: Double = 0.0,
    val rating: Float = 0f,
    val imageResource: Int = 0,
    val description: String = "",
    val location: String = ""
)

