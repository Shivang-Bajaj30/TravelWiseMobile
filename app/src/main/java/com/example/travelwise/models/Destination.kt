package com.example.travelwise.models

data class Destination(
    val id: Int = 0,
    val name: String = "",
    val location: String = "",
    val price: Double = 0.0,
    val rating: Float = 0f,
    val imageResource: Int = 0,
    val description: String = "",  // Add this comma
    var isFavorite: Boolean = false
)