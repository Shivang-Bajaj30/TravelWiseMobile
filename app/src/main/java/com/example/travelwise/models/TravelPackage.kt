package com.example.travelwise.models

data class TravelPackage(
    val id: Int = 0,
    val title: String = "",
    val flightClass: String = "", // e.g., "Business", "Economy"
    val hotelClass: String = "", // e.g., "Deluxe", "Standard"
    val startDate: String = "",
    val endDate: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "", // URL for image from Google/Pinterest
    val description: String = ""
)



