package com.example.travelwise.models

data class DayItinerary(
    val dayNumber: Int = 1,
    val date: String = "",
    val dateFull: String = "",
    val activities: List<ItineraryActivity> = emptyList(),
    val imageUrl: String = "" // Image URL for the day card
)

data class ItineraryActivity(
    val time: String = "",
    val title: String = "",
    val description: String = "",
    val type: ActivityType = ActivityType.GENERAL
)

enum class ActivityType {
    GENERAL,
    FLIGHT,
    HOTEL,
    ATTRACTION,
    MEAL,
    TRANSPORT;

    companion object {
        fun fromString(type: String?): ActivityType {
            return when (type?.uppercase()) {
                "FLIGHT" -> FLIGHT
                "HOTEL" -> HOTEL
                "ATTRACTION" -> ATTRACTION
                "MEAL" -> MEAL
                "TRANSPORT" -> TRANSPORT
                else -> GENERAL
            }
        }
    }
}

