package com.example.travelwise.models

data class User(
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String
)

