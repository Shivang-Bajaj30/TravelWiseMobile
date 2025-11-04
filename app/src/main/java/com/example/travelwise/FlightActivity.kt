package com.example.travelwise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwise.adapters.FlightAdapter
import com.example.travelwise.databinding.ActivityFlightBinding
import com.example.travelwise.models.Flight

class FlightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlightBinding
    private lateinit var flightAdapter: FlightAdapter
    private val allFlights = mutableListOf<Flight>()
    private val displayedFlights = mutableListOf<Flight>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Prevent search bars from auto-focusing
        binding.etFrom.clearFocus()
        binding.etTo.clearFocus()
        binding.root.requestFocus()

        // Setup RecyclerView first (adapter must be initialized before search triggers)
        setupRecyclerView()

        // Load sample flight data
        loadSampleFlights()

        // Setup search functionality (after adapter initialized)
        setupSearch()
    }

    private fun setupSearch() {
        // Swap button functionality
        binding.btnSwap.setOnClickListener {
            val fromText = binding.etFrom.text.toString()
            val toText = binding.etTo.text.toString()
            
            binding.etFrom.setText(toText)
            binding.etTo.setText(fromText)
            
            // Filter flights after swap
            filterFlights()
        }

        // Search button functionality
        binding.btnSearchFlights.setOnClickListener {
            filterFlights()
        }

        // Dynamic search on from field
        binding.etFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterFlights()
            }
        })

        // Dynamic search on to field
        binding.etTo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterFlights()
            }
        })

        // Set default values
        binding.etFrom.setText("Bangalore")
        binding.etTo.setText("")
    }

    private fun filterFlights() {
        val from = binding.etFrom.text.toString().trim().lowercase()
        val to = binding.etTo.text.toString().trim().lowercase()

        displayedFlights.clear()

        if (from.isEmpty() && to.isEmpty()) {
            // Show all flights if both are empty
            displayedFlights.addAll(allFlights)
        } else {
            // Filter flights based on from and to
            displayedFlights.addAll(
                allFlights.filter { flight ->
                    val matchesFrom = from.isEmpty() || 
                                    flight.from.lowercase().contains(from) ||
                                    flight.airline.lowercase().contains(from)
                    val matchesTo = to.isEmpty() || 
                                  flight.to.lowercase().contains(to)
                    matchesFrom && matchesTo
                }
            )
        }

        if (!::flightAdapter.isInitialized) return
        flightAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        flightAdapter = FlightAdapter(displayedFlights) { flight ->
            Toast.makeText(this, "Flight ${flight.flightNumber} selected", Toast.LENGTH_SHORT).show()
            // You can add navigation to flight booking details here
        }

        binding.rvFlights.apply {
            layoutManager = LinearLayoutManager(this@FlightActivity)
            adapter = flightAdapter
        }
    }

    private fun loadSampleFlights() {
        allFlights.clear()
        allFlights.addAll(
            listOf(
                Flight(
                    id = 1,
                    airline = "Air India",
                    flightNumber = "AI 101",
                    from = "Bangalore",
                    to = "Mumbai",
                    departureTime = "08:30",
                    arrivalTime = "10:15",
                    duration = "1h 45m",
                    price = 4500.0,
                    stops = 0,
                    aircraft = "Airbus A320"
                ),
                Flight(
                    id = 2,
                    airline = "Indigo",
                    flightNumber = "6E 234",
                    from = "Bangalore",
                    to = "Delhi",
                    departureTime = "09:00",
                    arrivalTime = "11:30",
                    duration = "2h 30m",
                    price = 5500.0,
                    stops = 0,
                    aircraft = "Airbus A320neo"
                ),
                Flight(
                    id = 3,
                    airline = "Vistara",
                    flightNumber = "UK 456",
                    from = "Bangalore",
                    to = "Goa",
                    departureTime = "10:15",
                    arrivalTime = "11:00",
                    duration = "45m",
                    price = 3500.0,
                    stops = 0,
                    aircraft = "Airbus A321"
                ),
                Flight(
                    id = 4,
                    airline = "Emirates",
                    flightNumber = "EK 789",
                    from = "Bangalore",
                    to = "Dubai",
                    departureTime = "14:00",
                    arrivalTime = "17:30",
                    duration = "3h 30m",
                    price = 25000.0,
                    stops = 0,
                    aircraft = "Airbus A380"
                ),
                Flight(
                    id = 5,
                    airline = "Air India",
                    flightNumber = "AI 201",
                    from = "Bangalore",
                    to = "Kolkata",
                    departureTime = "11:30",
                    arrivalTime = "14:00",
                    duration = "2h 30m",
                    price = 6000.0,
                    stops = 0,
                    aircraft = "Airbus A319"
                ),
                Flight(
                    id = 6,
                    airline = "Indigo",
                    flightNumber = "6E 567",
                    from = "Bangalore",
                    to = "Hyderabad",
                    departureTime = "12:00",
                    arrivalTime = "13:15",
                    duration = "1h 15m",
                    price = 3200.0,
                    stops = 0,
                    aircraft = "Airbus A320"
                ),
                Flight(
                    id = 7,
                    airline = "Vistara",
                    flightNumber = "UK 890",
                    from = "Bangalore",
                    to = "Pune",
                    departureTime = "15:30",
                    arrivalTime = "16:45",
                    duration = "1h 15m",
                    price = 3800.0,
                    stops = 0,
                    aircraft = "Airbus A320neo"
                ),
                Flight(
                    id = 8,
                    airline = "Emirates",
                    flightNumber = "EK 345",
                    from = "Bangalore",
                    to = "Singapore",
                    departureTime = "16:00",
                    arrivalTime = "21:30",
                    duration = "5h 30m",
                    price = 32000.0,
                    stops = 0,
                    aircraft = "Airbus A350"
                ),
                Flight(
                    id = 9,
                    airline = "Air India",
                    flightNumber = "AI 301",
                    from = "Bangalore",
                    to = "Chennai",
                    departureTime = "07:00",
                    arrivalTime = "08:00",
                    duration = "1h",
                    price = 2800.0,
                    stops = 0,
                    aircraft = "Airbus A319"
                ),
                Flight(
                    id = 10,
                    airline = "Indigo",
                    flightNumber = "6E 678",
                    from = "Bangalore",
                    to = "Kochi",
                    departureTime = "13:00",
                    arrivalTime = "14:15",
                    duration = "1h 15m",
                    price = 3000.0,
                    stops = 0,
                    aircraft = "Airbus A320"
                )
            )
        )

        // Initialize displayed flights with all flights
        displayedFlights.clear()
        displayedFlights.addAll(allFlights)
        flightAdapter.notifyDataSetChanged()
    }
}

