package com.example.travelwise

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwise.databinding.ActivityTripsBinding
import com.example.travelwise.ui.home.HomeActivity

class TripsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripsBinding
    private val trips = mutableListOf<Any>() // Will hold trip data later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupUI()
        setupBottomNavigation()
        checkTrips()
    }

    private fun setupUI() {
        // Setup RecyclerView (for future use)
        binding.rvTrips.apply {
            layoutManager = LinearLayoutManager(this@TripsActivity)
            // adapter will be set when we have trips
        }

        // Setup Explore button click
        binding.btnExploreDestinations.setOnClickListener {
            navigateToHome()
        }
    }

    private fun checkTrips() {
        // Check if user has any trips
        if (trips.isEmpty()) {
            showEmptyState()
        } else {
            showTripsList()
        }
    }

    private fun showEmptyState() {
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.rvTrips.visibility = View.GONE
    }

    private fun showTripsList() {
        binding.emptyStateLayout.visibility = View.GONE
        binding.rvTrips.visibility = View.VISIBLE
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_trips

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_trips -> true
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