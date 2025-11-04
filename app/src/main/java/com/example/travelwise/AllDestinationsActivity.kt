package com.example.travelwise

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwise.adapters.DestinationAdapter
import com.example.travelwise.databinding.ActivityAllDestinationsBinding
import com.example.travelwise.models.Destination
import kotlinx.coroutines.launch

class AllDestinationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllDestinationsBinding
    private lateinit var destinationAdapter: DestinationAdapter
    private val allDestinations = mutableListOf<Destination>()
    private val displayedDestinations = mutableListOf<Destination>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDestinationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.root.post {
            binding.etSearch.clearFocus()
            binding.root.requestFocus()
        }

        setupRecyclerView()
        loadAllDestinations()
        setupSearch()
    }

    private fun setupRecyclerView() {
        destinationAdapter = DestinationAdapter(displayedDestinations) { destination ->
            openDestinationDetail(destination)
        }

        // Use GridLayoutManager for 2 columns on larger screens, 1 column on smaller screens
        val spanCount = if (resources.configuration.screenWidthDp >= 600) 2 else 1
        binding.rvAllDestinations.apply {
            layoutManager = GridLayoutManager(this@AllDestinationsActivity, spanCount)
            adapter = destinationAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterDestinations(s.toString())
            }
        })
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

    private fun loadAllDestinations() {
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
}

