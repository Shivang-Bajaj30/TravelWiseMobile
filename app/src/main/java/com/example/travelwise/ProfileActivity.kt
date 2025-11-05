package com.example.travelwise

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.travelwise.databinding.ActivityProfileBinding
import com.example.travelwise.ui.home.HomeActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupProfile()
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupProfile() {
        // Get username from SharedPreferences or intent
        val sharedPref = getSharedPreferences("TravelWisePrefs", MODE_PRIVATE)
        val username = sharedPref.getString("USERNAME", "Traveler") ?: "Traveler"
        val email = sharedPref.getString("EMAIL", "traveler@example.com") ?: "traveler@example.com"

        binding.tvUserName.text = username.replaceFirstChar { it.uppercase() }
        binding.tvUserEmail.text = email
    }

    private fun setupClickListeners() {
        // Edit Profile
        binding.layoutEditProfile.setOnClickListener {
            // TODO: Navigate to edit profile screen
        }

        // My Bookings
        binding.layoutMyBookings.setOnClickListener {
            // TODO: Navigate to bookings screen
        }

        // Payment Methods
        binding.layoutPaymentMethods.setOnClickListener {
            // TODO: Navigate to payment methods screen
        }

        // Settings
        binding.layoutSettings.setOnClickListener {
            // TODO: Navigate to settings screen
        }

        // Help & Support
        binding.layoutHelpSupport.setOnClickListener {
            // TODO: Navigate to help & support screen
        }

        // Logout
        binding.layoutLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        // Clear user session
        val sharedPref = getSharedPreferences("TravelWisePrefs", MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        // Navigate to login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
        // FIXED: Changed from bottomNavigation to bottomNavigation (underscore)
        binding.bottomNavigation.selectedItemId = R.id.nav_profile

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    finish() // Add finish() here to prevent activity stack buildup
                    true
                }
                R.id.nav_trips -> {
                    startActivity(Intent(this, TripsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }
}