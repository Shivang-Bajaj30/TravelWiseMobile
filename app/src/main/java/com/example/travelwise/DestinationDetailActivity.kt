package com.example.travelwise

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwise.adapters.ImagePagerAdapter
import com.example.travelwise.adapters.ItineraryAdapter
import com.example.travelwise.adapters.PopularPlacesAdapter
import com.example.travelwise.databinding.ActivityDestinationDetailBinding
import com.example.travelwise.models.ItineraryItem
import com.example.travelwise.models.PopularPlace
import com.example.travelwise.utils.FavoritesManager
import com.google.android.material.tabs.TabLayoutMediator

class DestinationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDestinationDetailBinding
    private lateinit var favoritesManager: FavoritesManager
    private var destinationId: Int = 0

    // Sample data - replace with actual data from intent
    private val destinationImages = listOf(
        R.drawable.image2,
        R.drawable.image3,
        R.drawable.image4,
        R.drawable.image5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDestinationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize favorites manager
        favoritesManager = FavoritesManager(this)

        // Hide action bar
        supportActionBar?.hide()

        // Get data from intent
        loadDestinationData()

        // Setup image viewpager
        setupImageViewPager()

        // Setup click listeners
        setupClickListeners()

        // Setup RecyclerViews
        setupItinerary()
        setupPopularPlaces()

        // Update favorite button state
        updateFavoriteButton()
    }

    private fun loadDestinationData() {
        // Get data passed from previous activity
        destinationId = intent.getIntExtra("DESTINATION_ID", 0)
        val name = intent.getStringExtra("DESTINATION_NAME") ?: "Matsumoto Castle"
        val location = intent.getStringExtra("DESTINATION_LOCATION") ?: "Osaka, Japan"
        val price = intent.getDoubleExtra("DESTINATION_PRICE", 130.0)
        val rating = intent.getFloatExtra("DESTINATION_RATING", 4.8f)
        val description = intent.getStringExtra("DESTINATION_DESC")
            ?: "Beautiful historic castle with stunning architecture and rich cultural heritage. Perfect destination for history enthusiasts and photography lovers."

        // Set data to views
        binding.tvDestinationName.text = name
        binding.tvLocation.text = location
        binding.tvPrice.text = "₹${String.format("%.2f", price * 100)}"
        binding.tvRating.text = rating.toString()
        binding.tvDescription.text = description

        // These would ideally come from an API or database
        binding.tvDistance.text = "2.3 KM"
        binding.tvTemperature.text = "17°C"
    }

    private fun setupImageViewPager() {
        val adapter = ImagePagerAdapter(destinationImages)
        binding.imageViewPager.adapter = adapter

        // Connect TabLayout with ViewPager2 for indicators
        TabLayoutMediator(binding.imageIndicator, binding.imageViewPager) { _, _ -> }.attach()
    }

    private fun setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Share button
        binding.btnShare.setOnClickListener {
            Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show()
            // TODO: Implement share functionality
        }

        // Favorite button
        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }

        // Get Started button
        binding.btnGetStarted.setOnClickListener {
            Toast.makeText(this, "Proceeding to booking...", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to booking/checkout page
        }
    }

    private fun toggleFavorite() {
        if (favoritesManager.isFavorite(destinationId)) {
            favoritesManager.removeFavorite(destinationId)
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
        } else {
            favoritesManager.addFavorite(destinationId)
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
        }
        updateFavoriteButton()
    }

    private fun updateFavoriteButton() {
        if (favoritesManager.isFavorite(destinationId)) {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun setupItinerary() {
        // Sample itinerary data
        val itineraryItems = listOf(
            ItineraryItem("Day 1", "Arrival and Castle Tour", "Explore the historic castle and surrounding gardens"),
            ItineraryItem("Day 2", "Local Culture Experience", "Visit traditional markets and museums"),
            ItineraryItem("Day 3", "Nature and Scenery", "Mountain hiking and scenic photography"),
            ItineraryItem("Day 4", "Departure", "Last minute shopping and departure")
        )

        binding.rvItinerary.layoutManager = LinearLayoutManager(this)
        binding.rvItinerary.adapter = ItineraryAdapter(itineraryItems)
    }

    private fun setupPopularPlaces() {
        // Sample popular places data
        val popularPlaces = listOf(
            PopularPlace("Castle Main Tower", R.drawable.image2, "4.9"),
            PopularPlace("Japanese Garden", R.drawable.image3, "4.7"),
            PopularPlace("Historic Museum", R.drawable.image4, "4.6"),
            PopularPlace("Traditional Market", R.drawable.image5, "4.5")
        )

        binding.rvPopularPlaces.layoutManager = LinearLayoutManager(this)
        binding.rvPopularPlaces.adapter = PopularPlacesAdapter(popularPlaces)
    }
}