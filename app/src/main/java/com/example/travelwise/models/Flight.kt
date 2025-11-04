package com.example.travelwise.models

data class Flight(
    val id: Int = 0,
    val airline: String = "",
    val flightNumber: String = "",
    val from: String = "",
    val to: String = "",
    val departureTime: String = "",
    val arrivalTime: String = "",
    val duration: String = "",
    val price: Double = 0.0,
    val stops: Int = 0,
    val aircraft: String = ""
)

