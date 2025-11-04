package com.example.travelwise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwise.adapters.HotelAdapter
import com.example.travelwise.databinding.ActivityHotelBinding
import com.example.travelwise.models.Hotel

class HotelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHotelBinding
    private lateinit var hotelAdapter: HotelAdapter
    private val allHotels = mutableListOf<Hotel>()
    private val displayedHotels = mutableListOf<Hotel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelBinding.inflate(layoutInflater)
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
        loadSampleHotels()
        setupSearch()
    }

    private fun setupRecyclerView() {
        hotelAdapter = HotelAdapter(displayedHotels) { hotel ->
            Toast.makeText(this, "${hotel.name} selected", Toast.LENGTH_SHORT).show()
        }

        binding.rvHotels.apply {
            layoutManager = LinearLayoutManager(this@HotelActivity)
            adapter = hotelAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterHotels(s.toString())
            }
        })
    }

    private fun filterHotels(query: String) {
        val searchQuery = query.trim().lowercase()
        
        displayedHotels.clear()
        
        if (searchQuery.isEmpty()) {
            displayedHotels.addAll(allHotels)
        } else {
            displayedHotels.addAll(
                allHotels.filter { hotel ->
                    hotel.name.lowercase().contains(searchQuery) ||
                    hotel.location.lowercase().contains(searchQuery)
                }
            )
        }
        
        hotelAdapter.notifyDataSetChanged()
    }

    private fun loadSampleHotels() {
        allHotels.clear()
        allHotels.addAll(
            listOf(
                Hotel(
                    id = 1,
                    name = "Taj Palace",
                    location = "Mumbai, Maharashtra",
                    price = 8500.0,
                    rating = 4.8f,
                    imageResource = R.drawable.ic_hotel,
                    description = "Luxury 5-star hotel",
                    amenities = listOf("WiFi", "Pool", "Spa", "Gym"),
                    distance = "2.5 km from airport"
                ),
                Hotel(
                    id = 2,
                    name = "Oberoi Hotel",
                    location = "Delhi, NCR",
                    price = 12000.0,
                    rating = 4.9f,
                    imageResource = R.drawable.ic_hotel,
                    description = "Premium luxury hotel",
                    amenities = listOf("WiFi", "Pool", "Spa", "Restaurant", "Bar"),
                    distance = "15 km from city center"
                ),
                Hotel(
                    id = 3,
                    name = "ITC Grand Chola",
                    location = "Chennai, Tamil Nadu",
                    price = 9500.0,
                    rating = 4.7f,
                    imageResource = R.drawable.ic_hotel,
                    description = "5-star luxury hotel",
                    amenities = listOf("WiFi", "Pool", "Spa", "Gym", "Parking"),
                    distance = "8 km from beach"
                ),
                Hotel(
                    id = 4,
                    name = "Leela Palace",
                    location = "Bangalore, Karnataka",
                    price = 11000.0,
                    rating = 4.9f,
                    imageResource = R.drawable.ic_hotel,
                    description = "Luxury palace hotel",
                    amenities = listOf("WiFi", "Pool", "Spa", "Gym", "Restaurant"),
                    distance = "5 km from airport"
                ),
                Hotel(
                    id = 5,
                    name = "JW Marriott",
                    location = "Goa",
                    price = 7500.0,
                    rating = 4.6f,
                    imageResource = R.drawable.ic_hotel,
                    description = "Beachfront luxury hotel",
                    amenities = listOf("WiFi", "Pool", "Beach Access", "Spa"),
                    distance = "On the beach"
                ),
                Hotel(
                    id = 6,
                    name = "Hyatt Regency",
                    location = "Pune, Maharashtra",
                    price = 6500.0,
                    rating = 4.5f,
                    imageResource = R.drawable.ic_hotel,
                    description = "Business luxury hotel",
                    amenities = listOf("WiFi", "Pool", "Gym", "Business Center"),
                    distance = "3 km from city center"
                ),
                Hotel(
                    id = 7,
                    name = "Radisson Blu",
                    location = "Kolkata, West Bengal",
                    price = 5500.0,
                    rating = 4.4f,
                    imageResource = R.drawable.ic_hotel,
                    description = "4-star hotel",
                    amenities = listOf("WiFi", "Pool", "Restaurant", "Parking"),
                    distance = "12 km from airport"
                ),
                Hotel(
                    id = 8,
                    name = "The Lalit",
                    location = "Jaipur, Rajasthan",
                    price = 6000.0,
                    rating = 4.5f,
                    imageResource = R.drawable.ic_hotel,
                    description = "Heritage luxury hotel",
                    amenities = listOf("WiFi", "Pool", "Spa", "Heritage Tours"),
                    distance = "10 km from palace"
                )
            )
        )

        // Initialize displayed hotels with all hotels
        displayedHotels.clear()
        displayedHotels.addAll(allHotels)
        hotelAdapter.notifyDataSetChanged()
    }
}

