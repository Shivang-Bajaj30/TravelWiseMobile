package com.example.travelwise.models

data class Car(
    val id: Int = 0,
    val name: String = "",
    val type: String = "",
    val price: Double = 0.0,
    val rating: Float = 0f,
    val imageResource: Int = 0,
    val description: String = "",
    val features: List<String> = emptyList(),
    val transmission: String = ""
)

