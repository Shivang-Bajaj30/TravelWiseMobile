package com.example.travelwise

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwise.adapters.DestinationAdapter
import com.example.travelwise.databinding.ActivityFavoritesBinding
import com.example.travelwise.models.Destination
import com.example.travelwise.ui.home.HomeActivity
import com.example.travelwise.utils.FavoritesManager

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var favoritesManager: FavoritesManager
    private lateinit var adapter: DestinationAdapter
    private val allDestinations = mutableListOf<Destination>()
    private val displayedDestinations = mutableListOf<Destination>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        favoritesManager = FavoritesManager(this)

        // Prevent search bar from auto-focusing
//        binding.root.post {
//            binding.etSearch.clearFocus()
//            binding.root.requestFocus()
//        }

        setupRecyclerView()
//        setupSearch()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun setupRecyclerView() {
        adapter = DestinationAdapter(
            destinations = displayedDestinations,
            onItemClick = { destination ->
                openDestinationDetail(destination)
            }
        )

        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(this@FavoritesActivity)
            adapter = this@FavoritesActivity.adapter
        }
    }

//    private fun setupSearch() {
//        binding.etSearch.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                filterDestinations(s.toString())
//            }
//        })
//    }

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
        
        adapter.notifyDataSetChanged()
        
        // Update empty state visibility
        if (displayedDestinations.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvFavorites.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE
        }
    }

    private fun loadFavorites() {
        val favoriteIds = favoritesManager.getFavorites()
        val destinationsList = getAllDestinations()

        allDestinations.clear()
        allDestinations.addAll(destinationsList.filter { favoriteIds.contains(it.id) })

        // Initialize displayed destinations with all favorites
        displayedDestinations.clear()
        displayedDestinations.addAll(allDestinations)

        if (displayedDestinations.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvFavorites.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE
        }

        adapter.notifyDataSetChanged()
    }

    private fun getAllDestinations(): List<Destination> {
        return listOf(
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
            )
        )
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
        // FIXED: Changed from bottomNavigation to bottomNavigation (to match XML)
        binding.bottomNavigation.selectedItemId = R.id.nav_favorites

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // FIXED: Properly navigate to home instead of just finishing
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_favorites -> true
                R.id.nav_trips -> {
                    startActivity(Intent(this, TripsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    // FIXED: Added navigation to profile
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}