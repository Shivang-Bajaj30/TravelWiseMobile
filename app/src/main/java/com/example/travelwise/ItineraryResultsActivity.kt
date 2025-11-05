package com.example.travelwise

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.travelwise.adapters.DayItineraryAdapter
import com.example.travelwise.databinding.ActivityItineraryResultsBinding
import com.example.travelwise.models.ActivityType
import com.example.travelwise.models.DayItinerary
import com.example.travelwise.models.ItineraryActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ItineraryResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItineraryResultsBinding
    private lateinit var dayItineraryAdapter: DayItineraryAdapter
    private val dayItineraries = mutableListOf<DayItinerary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItineraryResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Get data from intent
        val destination = intent.getStringExtra("DESTINATION") ?: ""
        val startDate = intent.getStringExtra("START_DATE") ?: ""
        val endDate = intent.getStringExtra("END_DATE") ?: ""
        val peopleCount = intent.getStringExtra("PEOPLE_COUNT") ?: "1"
        val itineraryText = intent.getStringExtra("ITINERARY_TEXT") ?: ""
        val origin = intent.getStringExtra("ORIGIN") ?: ""

        // Setup UI
        setupBackButton()
        setupHeader(destination, startDate, endDate, peopleCount)
        setupRecyclerView()
        parseAndDisplayItinerary(destination, startDate, endDate, itineraryText)
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupHeader(
        destination: String,
        startDate: String,
        endDate: String,
        peopleCount: String
    ) {
        binding.tvDestinationName.text = destination.ifEmpty { "Destination" }
        
        // Format dates for display
        val formattedDates = if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            "$startDate - $endDate"
        } else if (startDate.isNotEmpty()) {
            startDate
        } else if (endDate.isNotEmpty()) {
            endDate
        } else {
            ""
        }
        
        val details = buildString {
            if (formattedDates.isNotEmpty()) {
                append(formattedDates)
            }
            if (peopleCount.isNotEmpty()) {
                if (isNotEmpty()) append(" • ")
                append("$peopleCount Person${if (peopleCount != "1") "s" else ""}")
            }
        }
        
        binding.tvTripDetails.text = details
        
        // Load destination image
        val imageUrl = getDestinationImageUrl(destination)
        Glide.with(this)
            .load(imageUrl)
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
            clipToPadding = false
            clipChildren = false
        }
    }

    private fun parseAndDisplayItinerary(
        destination: String,
        startDate: String,
        endDate: String,
        itineraryText: String
    ) {
        dayItineraries.clear()
        
        // Always generate days based on date range first
        val generatedDays = generateSampleItinerary(destination, startDate, endDate)
        
        if (itineraryText.isNotEmpty()) {
            // Try to parse AI response and enhance the generated days
            val parsedActivities = parseItineraryText(itineraryText, startDate, endDate)
            
            // If parsing succeeded and found activities, merge them with generated days
            if (parsedActivities.isNotEmpty() && parsedActivities.size >= generatedDays.size) {
                // Replace with parsed days if they have more detail
                dayItineraries.addAll(parsedActivities)
            } else {
                // Use generated days, but try to enhance with parsed activities
                val enhancedDays = generatedDays.mapIndexed { index, day ->
                    if (index < parsedActivities.size && parsedActivities[index].activities.isNotEmpty()) {
                        day.copy(activities = parsedActivities[index].activities)
                    } else {
                        day
                    }
                }
                dayItineraries.addAll(enhancedDays)
            }
        } else {
            // No AI response, use generated days
            dayItineraries.addAll(generatedDays)
        }
        
        dayItineraryAdapter.notifyDataSetChanged()
    }

    private fun parseItineraryText(
        text: String,
        startDate: String,
        endDate: String
    ): List<DayItinerary> {
        val days = mutableListOf<DayItinerary>()
        
        // Try to parse day-by-day structure from AI response
        val lines = text.split("\n")
        var currentDay = 1
        var currentDate = startDate
        var currentActivities = mutableListOf<ItineraryActivity>()
        
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        // Try to parse start date
        try {
            if (startDate.isNotEmpty()) {
                val parsedDate = dateFormat.parse(startDate)
                if (parsedDate != null) {
                    calendar.time = parsedDate
                } else {
                    // Fallback to manual parsing
                    val parts = startDate.split(" ")
                    if (parts.size >= 3) {
                        calendar.set(Calendar.DAY_OF_MONTH, parts[0].toIntOrNull() ?: 1)
                        val monthName = parts[1]
                        val monthMap = mapOf(
                            "Jan" to 0, "Feb" to 1, "Mar" to 2, "Apr" to 3,
                            "May" to 4, "Jun" to 5, "Jul" to 6, "Aug" to 7,
                            "Sep" to 8, "Oct" to 9, "Nov" to 10, "Dec" to 11
                        )
                        calendar.set(Calendar.MONTH, monthMap[monthName] ?: 0)
                        calendar.set(Calendar.YEAR, parts[2].toIntOrNull() ?: 2025)
                    }
                }
            }
        } catch (e: Exception) {
            // Use default date
        }
        
        var inDaySection = false
        
        for (line in lines) {
            val trimmed = line.trim()
            
            // Check for day markers
            if (trimmed.matches(Regex("(?i)(day|day\\s*\\d+)"))) {
                // Save previous day if exists
                if (currentActivities.isNotEmpty()) {
                    val imageUrl = getDayImageUrl("", currentDay)
                    days.add(createDayItinerary(currentDay, currentDate, currentActivities, imageUrl))
                    currentDay++
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    currentDate = formatDate(calendar)
                    currentActivities = mutableListOf()
                }
                inDaySection = true
                continue
            }
            
            // Check for time patterns (e.g., "09:00", "9 AM", "Morning")
            if (inDaySection && trimmed.matches(Regex(".*\\d{1,2}[:\\.]\\d{2}.*|.*(morning|afternoon|evening|night).*", RegexOption.IGNORE_CASE))) {
                val activity = parseActivityLine(trimmed)
                if (activity != null) {
                    currentActivities.add(activity)
                }
            } else if (inDaySection && trimmed.length > 20 && !trimmed.startsWith("-") && !trimmed.startsWith("*")) {
                // Long lines might be activity descriptions
                val activity = ItineraryActivity(
                    time = "",
                    title = trimmed.take(50),
                    description = trimmed,
                    type = ActivityType.GENERAL
                )
                currentActivities.add(activity)
            }
        }
        
        // Add last day
        if (currentActivities.isNotEmpty() || days.isEmpty()) {
            val imageUrl = getDayImageUrl("", currentDay)
            days.add(createDayItinerary(currentDay, currentDate, currentActivities, imageUrl))
        }
        
        // If no days were parsed, generate sample
        if (days.isEmpty()) {
            return generateSampleItinerary("", startDate, endDate)
        }
        
        return days
    }

    private fun parseActivityLine(line: String): ItineraryActivity? {
        // Try to extract time and activity
        val timePattern = Regex("(\\d{1,2}[:\\.]\\d{2}|\\d{1,2}\\s*(AM|PM|am|pm))")
        val match = timePattern.find(line)
        
        val time = match?.value ?: ""
        val title = line.replace(timePattern, "").trim().take(100)
        
        if (title.isEmpty()) return null
        
        val type = when {
            title.contains("flight", ignoreCase = true) || 
            title.contains("airport", ignoreCase = true) -> ActivityType.FLIGHT
            title.contains("hotel", ignoreCase = true) || 
            title.contains("stay", ignoreCase = true) -> ActivityType.HOTEL
            title.contains("museum", ignoreCase = true) || 
            title.contains("temple", ignoreCase = true) || 
            title.contains("beach", ignoreCase = true) -> ActivityType.ATTRACTION
            title.contains("restaurant", ignoreCase = true) || 
            title.contains("meal", ignoreCase = true) -> ActivityType.MEAL
            title.contains("bus", ignoreCase = true) || 
            title.contains("taxi", ignoreCase = true) -> ActivityType.TRANSPORT
            else -> ActivityType.GENERAL
        }
        
        return ItineraryActivity(
            time = time,
            title = title,
            description = line,
            type = type
        )
    }

    private fun createDayItinerary(
        dayNumber: Int,
        date: String,
        activities: List<ItineraryActivity>,
        imageUrl: String = ""
    ): DayItinerary {
        val parts = date.split(" ")
        val dateShort = if (parts.size >= 2) "${parts[0]} ${parts[1]}" else date
        
        return DayItinerary(
            dayNumber = dayNumber,
            date = dateShort,
            dateFull = date,
            activities = if (activities.isEmpty()) {
                listOf(
                    ItineraryActivity(
                        time = "09:00",
                        title = "Explore Destination",
                        description = "Discover the beauty and culture of this amazing destination",
                        type = ActivityType.GENERAL
                    )
                )
            } else {
                activities
            },
            imageUrl = imageUrl
        )
    }

    private fun formatDate(calendar: Calendar): String {
        val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return format.format(calendar.time)
    }

    private fun generateSampleItinerary(
        destination: String,
        startDate: String,
        endDate: String
    ): List<DayItinerary> {
        val days = mutableListOf<DayItinerary>()
        
        // Calculate number of days
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val start = try {
            if (startDate.isNotEmpty()) {
                // Try parsing with SimpleDateFormat first
                try {
                    dateFormat.parse(startDate) ?: Calendar.getInstance().time
                } catch (e: Exception) {
                    // Fallback to manual parsing
                    val parts = startDate.split(" ")
                    if (parts.size >= 3) {
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.DAY_OF_MONTH, parts[0].toIntOrNull() ?: 1)
                        val monthMap = mapOf(
                            "Jan" to 0, "Feb" to 1, "Mar" to 2, "Apr" to 3,
                            "May" to 4, "Jun" to 5, "Jul" to 6, "Aug" to 7,
                            "Sep" to 8, "Oct" to 9, "Nov" to 10, "Dec" to 11
                        )
                        calendar.set(Calendar.MONTH, monthMap[parts[1]] ?: 0)
                        calendar.set(Calendar.YEAR, parts[2].toIntOrNull() ?: 2025)
                        calendar.time
                    } else {
                        Calendar.getInstance().time
                    }
                }
            } else {
                Calendar.getInstance().time
            }
        } catch (e: Exception) {
            Calendar.getInstance().time
        }
        
        val end = try {
            if (endDate.isNotEmpty()) {
                // Try parsing with SimpleDateFormat first
                try {
                    dateFormat.parse(endDate) ?: start
                } catch (e: Exception) {
                    // Fallback to manual parsing
                    val parts = endDate.split(" ")
                    if (parts.size >= 3) {
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.DAY_OF_MONTH, parts[0].toIntOrNull() ?: 1)
                        val monthMap = mapOf(
                            "Jan" to 0, "Feb" to 1, "Mar" to 2, "Apr" to 3,
                            "May" to 4, "Jun" to 5, "Jul" to 6, "Aug" to 7,
                            "Sep" to 8, "Oct" to 9, "Nov" to 10, "Dec" to 11
                        )
                        calendar.set(Calendar.MONTH, monthMap[parts[1]] ?: 0)
                        calendar.set(Calendar.YEAR, parts[2].toIntOrNull() ?: 2025)
                        calendar.time
                    } else {
                        start
                    }
                }
            } else {
                start
            }
        } catch (e: Exception) {
            start
        }
        
        val diffInMillis = end.time - start.time
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        val numDays = diffInDays + 1 // Include both start and end day
        
        // Ensure we have at least 1 day
        if (numDays <= 0) {
            // Fallback: generate at least 1 day
            val calendar = Calendar.getInstance()
            calendar.time = start
            val date = formatDate(calendar)
            val activities = generateSampleActivities(1, destination)
            val imageUrl = getDayImageUrl(destination, 1)
            days.add(createDayItinerary(1, date, activities, imageUrl))
            return days
        }
        
        val calendar = Calendar.getInstance()
        calendar.time = start
        
        for (dayNum in 1..numDays) {
            val date = formatDate(calendar)
            val activities = generateSampleActivities(dayNum, destination)
            val imageUrl = getDayImageUrl(destination, dayNum)
            days.add(createDayItinerary(dayNum, date, activities, imageUrl))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return days
    }

    private fun generateSampleActivities(dayNumber: Int, destination: String): List<ItineraryActivity> {
        val baseActivities = listOf(
            ItineraryActivity("09:00", "Morning Activities", "Start your day exploring $destination", ActivityType.ATTRACTION),
            ItineraryActivity("13:00", "Lunch", "Enjoy local cuisine at a recommended restaurant", ActivityType.MEAL),
            ItineraryActivity("16:00", "Afternoon Exploration", "Continue discovering beautiful places", ActivityType.ATTRACTION),
            ItineraryActivity("19:00", "Dinner", "Experience fine dining at a local favorite", ActivityType.MEAL)
        )
        
        return when (dayNumber) {
            1 -> listOf(
                ItineraryActivity("09:00", "Arrival & Check-in", "Check into your hotel and freshen up", ActivityType.HOTEL),
                ItineraryActivity("12:00", "Lunch", "Enjoy local cuisine at a recommended restaurant", ActivityType.MEAL),
                ItineraryActivity("14:00", "Explore Downtown", "Take a leisurely walk through the city center", ActivityType.ATTRACTION),
                ItineraryActivity("18:00", "Dinner", "Experience fine dining at a local favorite", ActivityType.MEAL)
            )
            2 -> listOf(
                ItineraryActivity("08:00", "Morning Tour", "Visit famous landmarks and attractions", ActivityType.ATTRACTION),
                ItineraryActivity("13:00", "Lunch Break", "Rest and enjoy a meal", ActivityType.MEAL),
                ItineraryActivity("15:00", "Cultural Experience", "Immerse yourself in local culture", ActivityType.ATTRACTION),
                ItineraryActivity("19:00", "Evening Activities", "Enjoy the nightlife and entertainment", ActivityType.GENERAL)
            )
            else -> baseActivities
        }
    }

    private fun getDayImageUrl(destination: String, dayNumber: Int): String {
        // Generate different images for each day from Unsplash
        val destinationLower = destination.lowercase()
        val imageUrls = listOf(
            "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=400", // Cityscape
            "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?w=400", // Urban
            "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=400", // Travel
            "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=400", // Architecture
            "https://images.unsplash.com/photo-1519904981063-b0cf448d479e?w=400", // Landscape
            "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400", // Nature
            "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=400", // Beach
            "https://images.unsplash.com/photo-1511739001486-6bfe10ce785f?w=400", // Culture
            "https://images.unsplash.com/photo-1500835556837-99ac94a94552?w=400", // Architecture 2
            "https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=400", // Tropical
            "https://images.unsplash.com/photo-1580227974546-0b84c55d444e?w=400", // Coastal
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400", // Beach 2
            "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=400", // Resort
            "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=400", // Modern
            "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=400"  // Villa
        )
        
        // Use different images based on day number, cycling through the list
        return imageUrls[(dayNumber - 1) % imageUrls.size]
    }

    private fun getDestinationImageUrl(destination: String): String {
        // Get destination-specific images from Unsplash
        val destinationLower = destination.lowercase()
        return when {
            destinationLower.contains("goa") -> "https://images.unsplash.com/photo-1580227974546-0b84c55d444e?w=800"
            destinationLower.contains("bali") -> "https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=800"
            destinationLower.contains("tokyo") -> "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=800"
            destinationLower.contains("paris") -> "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800"
            destinationLower.contains("new york") -> "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?w=800"
            else -> "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=800" // Generic travel
        }
    }
}
