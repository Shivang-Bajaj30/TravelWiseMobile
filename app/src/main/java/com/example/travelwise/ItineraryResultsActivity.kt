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
import com.example.travelwise.models.ItineraryActivityExtended
import com.example.travelwise.models.HotelInfo
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
    @SerializedName("activities") val activities: List<Any>?
)

data class AIHotel(
    @SerializedName("name") val name: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("image") val image: String?
)

data class AIActivityExtended(
    @SerializedName("time") val time: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("image_source") val imageSource: String?,
    @SerializedName("image_credit") val imageCredit: String?,
    @SerializedName("hotel") val hotel: AIHotel?
)

class ItineraryResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItineraryResultsBinding
    private lateinit var dayItineraryAdapter: DayItineraryAdapter
    private lateinit var filteredAdapter: com.example.travelwise.adapters.FilteredActivityAdapter
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
        setupFilterBar()
        parseAndDisplayItinerary(itineraryJson)
    }

    private fun setupFilterBar() {
        val btnOverall = binding.root.findViewById<android.view.View>(R.id.btnFilterOverall)
        val btnHotels = binding.root.findViewById<android.view.View>(R.id.btnFilterHotels)
        val btnAttractions = binding.root.findViewById<android.view.View>(R.id.btnFilterAttractions)
        val btnFlights = binding.root.findViewById<android.view.View>(R.id.btnFilterFlights)
        val btnMeals = binding.root.findViewById<android.view.View>(R.id.btnFilterMeals)
        val btnTransport = binding.root.findViewById<android.view.View>(R.id.btnFilterTransport)

        filteredAdapter = com.example.travelwise.adapters.FilteredActivityAdapter(emptyList())
        val rvFiltered = binding.root.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvFilteredActivities)
        rvFiltered.layoutManager = LinearLayoutManager(this)
        rvFiltered.adapter = filteredAdapter

        btnOverall.setOnClickListener {
            // Show overall day-by-day
            binding.root.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvDayItinerary).visibility = android.view.View.VISIBLE
            rvFiltered.visibility = android.view.View.GONE
        }

        fun showFiltered(type: com.example.travelwise.models.ActivityType) {
            // Flatten all activities across days for the selected type
            val flat = dayItineraries.flatMap { it.activities }.filter { it.type == type }
            filteredAdapter.update(flat)
            binding.root.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvDayItinerary).visibility = android.view.View.GONE
            rvFiltered.visibility = android.view.View.VISIBLE
        }

        btnHotels.setOnClickListener { showFiltered(com.example.travelwise.models.ActivityType.HOTEL) }
        btnAttractions.setOnClickListener { showFiltered(com.example.travelwise.models.ActivityType.ATTRACTION) }
        btnFlights.setOnClickListener { showFiltered(com.example.travelwise.models.ActivityType.FLIGHT) }
        btnMeals.setOnClickListener { showFiltered(com.example.travelwise.models.ActivityType.MEAL) }
        btnTransport.setOnClickListener { showFiltered(com.example.travelwise.models.ActivityType.TRANSPORT) }
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
        val gson = Gson()
        return aiResponse?.itinerary?.mapNotNull { aiDay ->
            val rawList = aiDay?.activities as? List<*>
            val activities = rawList?.mapNotNull { rawAct ->
                when (rawAct) {
                    is Map<*, *> -> {
                        val title = rawAct["title"] as? String ?: rawAct["name"] as? String
                        if (title == null) return@mapNotNull null
                        val time = rawAct["time"] as? String ?: ""
                        val description = rawAct["description"] as? String ?: ""
                        val typeStr = rawAct["type"] as? String
                        val image = rawAct["image"] as? String ?: ""
                        val hotelMap = rawAct["hotel"] as? Map<*, *>
                        val hotel = if (hotelMap != null) {
                            HotelInfo(
                                name = hotelMap["name"] as? String ?: "",
                                address = hotelMap["address"] as? String ?: "",
                                image = hotelMap["image"] as? String ?: ""
                            )
                        } else null

                        ItineraryActivityExtended(
                            time = time,
                            title = title,
                            description = description,
                            type = ActivityType.fromString(typeStr),
                            image = image,
                            imageSource = rawAct["image_source"] as? String ?: "",
                            imageCredit = rawAct["image_credit"] as? String ?: "",
                            hotel = hotel
                        )
                    }
                    else -> {
                        try {
                            val json = gson.toJson(rawAct)
                            val act = gson.fromJson(json, AIActivityExtended::class.java)
                            if (act.title == null) return@mapNotNull null
                            val hotelInfo = act.hotel?.let { HotelInfo(it.name ?: "", it.address ?: "", it.image ?: "") }
                            ItineraryActivityExtended(
                                time = act.time ?: "",
                                title = act.title,
                                description = act.description ?: "",
                                type = ActivityType.fromString(act.type),
                                image = act.image ?: "",
                                imageSource = act.imageSource ?: "",
                                imageCredit = act.imageCredit ?: "",
                                hotel = hotelInfo
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
            } ?: emptyList()

            if (aiDay?.day == null || activities.isEmpty()) {
                null
            } else {
                createDayItinerary(aiDay.day, aiDay.date ?: "", activities)
            }
        } ?: emptyList()
    }

    private fun createDayItinerary(dayNumber: Int, date: String, activities: List<ItineraryActivityExtended>): DayItinerary {
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
