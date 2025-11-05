package com.example.travelwise.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwise.R
import com.example.travelwise.adapters.DestinationAdapter
import com.example.travelwise.databinding.ActivityHomeBinding
import com.example.travelwise.models.Destination
import com.example.travelwise.AllDestinationsActivity
import com.example.travelwise.AiTripPlanActivity
import com.example.travelwise.CarActivity
import com.example.travelwise.DestinationDetailActivity
import com.example.travelwise.FavoritesActivity
import com.example.travelwise.FlightActivity
import com.example.travelwise.HotelActivity
import com.example.travelwise.MealActivity
import com.example.travelwise.ProfileActivity
import com.example.travelwise.TripsActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var destinationAdapter: DestinationAdapter
    private val allDestinations = mutableListOf<Destination>()
    private val displayedDestinations = mutableListOf<Destination>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Resolve username from persistent storage, fallback to intent, then default
        val prefs = getSharedPreferences("TravelWisePrefs", MODE_PRIVATE)
        var storedUsername = prefs.getString("USERNAME", null)
        if (storedUsername.isNullOrBlank()) {
            val intentUsername = intent.getStringExtra("USERNAME")
            if (!intentUsername.isNullOrBlank()) {
                storedUsername = intentUsername
                prefs.edit().putString("USERNAME", storedUsername).apply()
            }
        }
        val effectiveUsername = (storedUsername ?: "Traveler")
            .split(".", "_", " ")[0]
            .replaceFirstChar { it.uppercase() }
        binding.tvGreeting.text = "Welcome, $effectiveUsername!"

        // Prevent search bar from auto-focusing on activity start
        // Use post to ensure it happens after layout is complete
        binding.root.post {
            binding.etSearch.clearFocus()
            binding.root.requestFocus()
        }

        // Setup category boxes
        setupCategoryBoxes()

        // Setup View All click listener
        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(this, AllDestinationsActivity::class.java))
        }

        // RecyclerView setup
        setupRecyclerView()

        // Load sample destinations
        loadSampleData()

        // Setup search submit action (no dynamic filtering)
        setupSearch()

        // Bottom navigation
        setupBottomNavigation()
    }

    private fun setupCategoryBoxes() {
        // Hotels - using View Binding
        binding.boxHotels.ivCategoryIcon.setImageResource(R.drawable.ic_hotel)
        binding.boxHotels.tvCategoryName.text = "Hotels"
        binding.boxHotels.root.setOnClickListener {
            startActivity(Intent(this, HotelActivity::class.java))
        }

        // Flights
        binding.boxFlights.ivCategoryIcon.setImageResource(R.drawable.ic_flight)
        binding.boxFlights.tvCategoryName.text = "Flights"
        binding.boxFlights.root.setOnClickListener {
            startActivity(Intent(this, FlightActivity::class.java))
        }

        // Cars
        binding.boxCars.ivCategoryIcon.setImageResource(R.drawable.ic_car)
        binding.boxCars.tvCategoryName.text = "Cars"
        binding.boxCars.root.setOnClickListener {
            startActivity(Intent(this, CarActivity::class.java))
        }

        // Meals
        binding.boxMeals.ivCategoryIcon.setImageResource(R.drawable.ic_meal)
        binding.boxMeals.tvCategoryName.text = "Meals"
        binding.boxMeals.root.setOnClickListener {
            startActivity(Intent(this, MealActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        destinationAdapter = DestinationAdapter(
            destinations = displayedDestinations,
            onItemClick = { destination ->
                openDestinationDetail(destination)
            }
        )

        binding.rvDestinations.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = destinationAdapter

            // Add horizontal spacing between cards
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.card_spacing)
            addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: android.graphics.Rect,
                    view: android.view.View,
                    parent: androidx.recyclerview.widget.RecyclerView,
                    state: androidx.recyclerview.widget.RecyclerView.State
                ) {
                    outRect.right = spacingInPixels
                }
            })
        }
    }

    private fun setupSearch() {
        // Navigate to AI Trip planner when user submits via keyboard action
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            val query = binding.etSearch.text?.toString()?.trim() ?: ""
            if (query.isNotEmpty()) {
                val intent = Intent(this, AiTripPlanActivity::class.java).apply {
                    putExtra("DESTINATION_QUERY", query)
                }
                startActivity(intent)
                true
            } else {
                false
            }
        }
    }

    private fun filterDestinations(query: String) {
        val searchQuery = query.trim().lowercase()
        
        displayedDestinations.clear()
        
        if (searchQuery.isEmpty()) {
            displayedDestinations.addAll(allDestinations)
        } else {
            displayedDestinations.addAll(
                allDestinations.filter { destination ->
                    destination.name.lowercase().contains(searchQuery) ||
                    destination.location.lowercase().contains(searchQuery)
                }
            )
        }
        
        destinationAdapter.notifyDataSetChanged()
    }

    private fun loadSampleData() {
        lifecycleScope.launch {
            allDestinations.clear()
            allDestinations.addAll(
                listOf(
                    Destination(
                        id = 1,
                        name = "Matsumoto Castle",
                        location = "Osaka, Japan",
                        price = 130.0,
                        rating = 4.8f,
                        imageResource = R.drawable.image11,
                        description = "Beautiful historic castle"
                    ),
                    Destination(
                        id = 2,
                        name = "Mountain Valley",
                        location = "Las Vegas, US",
                        price = 200.0,
                        rating = 4.9f,
                        imageResource = R.drawable.image12,
                        description = "Stunning mountain views"
                    ),
                    Destination(
                        id = 3,
                        name = "Tokyo Tower",
                        location = "Tokyo, Japan",
                        price = 150.0,
                        rating = 4.7f,
                        imageResource = R.drawable.image13,
                        description = "Iconic city landmark"
                    ),
                    Destination(
                        id = 4,
                        name = "Kyoto Temple",
                        location = "Kyoto, Japan",
                        price = 120.0,
                        rating = 4.9f,
                        imageResource = R.drawable.image14,
                        description = "Ancient temple complex"
                    ),
                    Destination(
                        id = 5,
                        name = "Beach Resort",
                        location = "Bali, Indonesia",
                        price = 180.0,
                        rating = 4.6f,
                        imageResource = R.drawable.image15,
                        description = "Tropical paradise"
                    ),
                    Destination(
                        id = 6,
                        name = "Taj Mahal",
                        location = "Agra, India",
                        price = 100.0,
                        rating = 4.9f,
                        imageResource = R.drawable.image11,
                        description = "Iconic marble mausoleum"
                    ),
                    Destination(
                        id = 7,
                        name = "Golden Temple",
                        location = "Amritsar, India",
                        price = 80.0,
                        rating = 4.8f,
                        imageResource = R.drawable.image12,
                        description = "Sacred Sikh gurdwara"
                    ),
                    Destination(
                        id = 8,
                        name = "Hawa Mahal",
                        location = "Jaipur, India",
                        price = 90.0,
                        rating = 4.7f,
                        imageResource = R.drawable.image13,
                        description = "Palace of Winds"
                    ),
                    Destination(
                        id = 9,
                        name = "Gateway of India",
                        location = "Mumbai, India",
                        price = 70.0,
                        rating = 4.6f,
                        imageResource = R.drawable.image14,
                        description = "Historic monument"
                    ),
                    Destination(
                        id = 10,
                        name = "Red Fort",
                        location = "Delhi, India",
                        price = 85.0,
                        rating = 4.7f,
                        imageResource = R.drawable.image15,
                        description = "UNESCO World Heritage Site"
                    ),
                    Destination(
                        id = 11,
                        name = "Varanasi Ghats",
                        location = "Varanasi, India",
                        price = 95.0,
                        rating = 4.8f,
                        imageResource = R.drawable.image11,
                        description = "Spiritual riverfront"
                    ),
                    Destination(
                        id = 12,
                        name = "Mysore Palace",
                        location = "Mysore, India",
                        price = 110.0,
                        rating = 4.7f,
                        imageResource = R.drawable.image12,
                        description = "Royal palace architecture"
                    )
                )
            )

            // Initialize displayed destinations with all destinations
            displayedDestinations.clear()
            displayedDestinations.addAll(allDestinations)
            destinationAdapter.notifyDataSetChanged()
        }
    }

    private fun openDestinationDetail(destination: Destination) {
        val intent = Intent(this, DestinationDetailActivity::class.java).apply {
            putExtra("DESTINATION_ID", destination.id)
            putExtra("DESTINATION_NAME", destination.name)
            putExtra("DESTINATION_LOCATION", destination.location)
            putExtra("DESTINATION_PRICE", destination.price)
            putExtra("DESTINATION_RATING", destination.rating)
            putExtra("DESTINATION_IMAGE", destination.imageResource)
            putExtra("DESTINATION_DESC", destination.description)
        }
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        // Set Home as selected
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_trips -> {
                    startActivity(Intent(this, TripsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}