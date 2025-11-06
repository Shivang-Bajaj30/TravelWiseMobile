package com.example.travelwise

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.travelwise.adapters.DayItineraryAdapter
import com.example.travelwise.databinding.ActivityItineraryResultsBinding
import com.example.travelwise.models.ActivityType
import com.example.travelwise.models.DayItinerary
import com.example.travelwise.models.ItineraryActivity
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.JsonSyntaxException

// Data classes for parsing the AI's JSON response
data class AIResponse(
    @SerializedName("itinerary") val itinerary: List<AIDay>?
)

data class AIDay(
    @SerializedName("day") val day: Int?,
    @SerializedName("date") val date: String?,
    @SerializedName("activities") val activities: List<AIActivity>?
)

data class AIActivity(
    @SerializedName("time") val time: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("type") val type: String?
)

class ItineraryResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItineraryResultsBinding
    private lateinit var dayItineraryAdapter: DayItineraryAdapter
    private val dayItineraries = mutableListOf<DayItinerary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItineraryResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val destination = intent.getStringExtra("DESTINATION") ?: ""
        val startDate = intent.getStringExtra("START_DATE") ?: ""
        val endDate = intent.getStringExtra("END_DATE") ?: ""
        val peopleCount = intent.getStringExtra("PEOPLE_COUNT") ?: "1"
        val itineraryJson = intent.getStringExtra("ITINERARY_TEXT") ?: ""

        setupBackButton()
        setupHeader(destination, startDate, endDate, peopleCount)
        setupRecyclerView()
        parseAndDisplayItinerary(itineraryJson)
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupHeader(destination: String, startDate: String, endDate: String, peopleCount: String) {
        binding.tvDestinationName.text = destination.ifEmpty { "Destination" }
        val formattedDates = if (startDate.isNotEmpty() && endDate.isNotEmpty()) "$startDate - $endDate" else startDate
        val details = "$formattedDates • $peopleCount Person${if (peopleCount != "1") "s" else ""}"
        binding.tvTripDetails.text = details
        Glide.with(this)
            .load(getDestinationImageUrl(destination))
            .placeholder(R.drawable.image11)
            .error(R.drawable.image11)
            .centerCrop()
            .into(binding.ivHeaderImage)
    }

    private fun setupRecyclerView() {
        dayItineraryAdapter = DayItineraryAdapter(dayItineraries)
        binding.rvDayItinerary.apply {
            layoutManager = LinearLayoutManager(this@ItineraryResultsActivity)
            adapter = dayItineraryAdapter
        }
    }

    private fun parseAndDisplayItinerary(itineraryJson: String) {
        dayItineraries.clear()
        if (itineraryJson.isNotBlank()) {
            try {
                val parsedDays = parseItineraryJson(itineraryJson)
                if (parsedDays.isNotEmpty()) {
                    dayItineraries.addAll(parsedDays)
                } else {
                    showError("Could not generate a detailed plan from the response.")
                }
            } catch (e: JsonSyntaxException) {
                Log.e("ItineraryResults", "Failed to parse JSON", e)
                showError("Failed to parse the AI's response. It was not valid JSON.")
            }
        } else {
            showError("No itinerary was generated.")
        }
        dayItineraryAdapter.notifyDataSetChanged()
    }

    private fun parseItineraryJson(json: String): List<DayItinerary> {
        // Clean the JSON string by removing markdown backticks if they exist
        val cleanedJson = json.removePrefix("```json").removeSuffix("```").trim()
        
        val aiResponse = Gson().fromJson(cleanedJson, AIResponse::class.java)
        
        return aiResponse?.itinerary?.mapNotNull { aiDay ->
            val activities = aiDay?.activities?.mapNotNull { aiActivity ->
                if (aiActivity?.title == null) null else ItineraryActivity(
                    time = aiActivity.time ?: "",
                    title = aiActivity.title,
                    description = aiActivity.description ?: "",
                    type = ActivityType.fromString(aiActivity.type)
                )
            } ?: emptyList()

            if (aiDay?.day == null || activities.isEmpty()) {
                null
            } else {
                createDayItinerary(aiDay.day, aiDay.date ?: "", activities)
            }
        } ?: emptyList()
    }

    private fun createDayItinerary(dayNumber: Int, date: String, activities: List<ItineraryActivity>): DayItinerary {
        val parts = date.split(" ")
        val dateShort = if (parts.size >= 2) "${parts[0]} ${parts[1]}" else date
        return DayItinerary(
            dayNumber = dayNumber,
            date = dateShort,
            dateFull = date,
            activities = activities,
            imageUrl = getDayImageUrl(dayNumber)
        )
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun getDayImageUrl(dayNumber: Int): String {
        val imageUrls = listOf(
            "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=400",
            "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?w=400",
            "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=400",
            "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=400",
            "https://images.unsplash.com/photo-1519904981063-b0cf448d479e?w=400",
            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400"
        )
        return imageUrls[(dayNumber - 1).coerceAtLeast(0) % imageUrls.size]
    }

    private fun getDestinationImageUrl(destination: String): String {
        val destinationLower = destination.lowercase()
        return when {
            destinationLower.contains("goa") -> "https://images.unsplash.com/photo-1580227974546-0b84c55d444e?w=800"
            destinationLower.contains("bali") -> "https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=800"
            destinationLower.contains("tokyo") -> "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=800"
            destinationLower.contains("paris") -> "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800"
            destinationLower.contains("new york") -> "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?w=800"
            else -> "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=800"
        }
    }
}
