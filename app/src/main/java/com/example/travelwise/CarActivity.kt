package com.example.travelwise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwise.adapters.CarAdapter
import com.example.travelwise.databinding.ActivityCarBinding
import com.example.travelwise.models.Car

class CarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarBinding
    private lateinit var carAdapter: CarAdapter
    private val allCars = mutableListOf<Car>()
    private val displayedCars = mutableListOf<Car>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarBinding.inflate(layoutInflater)
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
        loadSampleCars()
        setupSearch()
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter(displayedCars) { car ->
            Toast.makeText(this, "${car.name} selected", Toast.LENGTH_SHORT).show()
        }

        binding.rvCars.apply {
            layoutManager = LinearLayoutManager(this@CarActivity)
            adapter = carAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterCars(s.toString())
            }
        })
    }

    private fun filterCars(query: String) {
        val searchQuery = query.trim().lowercase()
        
        displayedCars.clear()
        
        if (searchQuery.isEmpty()) {
            displayedCars.addAll(allCars)
        } else {
            displayedCars.addAll(
                allCars.filter { car ->
                    car.name.lowercase().contains(searchQuery) ||
                    car.type.lowercase().contains(searchQuery)
                }
            )
        }
        
        carAdapter.notifyDataSetChanged()
    }

    private fun loadSampleCars() {
        allCars.clear()
        allCars.addAll(
            listOf(
                Car(
                    id = 1,
                    name = "Toyota Innova",
                    type = "SUV",
                    price = 2500.0,
                    rating = 4.6f,
                    imageResource = R.drawable.ic_car,
                    description = "Comfortable 7-seater",
                    features = listOf("AC", "Music", "GPS", "Bluetooth"),
                    transmission = "Manual"
                ),
                Car(
                    id = 2,
                    name = "Honda City",
                    type = "Sedan",
                    price = 1800.0,
                    rating = 4.5f,
                    imageResource = R.drawable.ic_car,
                    description = "Premium sedan",
                    features = listOf("AC", "Music", "GPS", "Sunroof"),
                    transmission = "Automatic"
                ),
                Car(
                    id = 3,
                    name = "Maruti Swift",
                    type = "Hatchback",
                    price = 1200.0,
                    rating = 4.4f,
                    imageResource = R.drawable.ic_car,
                    description = "Economical hatchback",
                    features = listOf("AC", "Music", "GPS"),
                    transmission = "Manual"
                ),
                Car(
                    id = 4,
                    name = "Mahindra XUV500",
                    type = "SUV",
                    price = 3000.0,
                    rating = 4.7f,
                    imageResource = R.drawable.ic_car,
                    description = "Luxury SUV",
                    features = listOf("AC", "Music", "GPS", "Leather Seats", "Sunroof"),
                    transmission = "Automatic"
                ),
                Car(
                    id = 5,
                    name = "Hyundai Creta",
                    type = "SUV",
                    price = 2200.0,
                    rating = 4.6f,
                    imageResource = R.drawable.ic_car,
                    description = "Compact SUV",
                    features = listOf("AC", "Music", "GPS", "Reverse Camera"),
                    transmission = "Automatic"
                ),
                Car(
                    id = 6,
                    name = "BMW 3 Series",
                    type = "Luxury Sedan",
                    price = 8000.0,
                    rating = 4.9f,
                    imageResource = R.drawable.ic_car,
                    description = "Premium luxury sedan",
                    features = listOf("AC", "Premium Sound", "GPS", "Leather", "Sunroof"),
                    transmission = "Automatic"
                ),
                Car(
                    id = 7,
                    name = "Mercedes-Benz E-Class",
                    type = "Luxury Sedan",
                    price = 10000.0,
                    rating = 4.9f,
                    imageResource = R.drawable.ic_car,
                    description = "Ultra luxury sedan",
                    features = listOf("AC", "Premium Sound", "GPS", "Massage Seats", "Panoramic Roof"),
                    transmission = "Automatic"
                ),
                Car(
                    id = 8,
                    name = "Tata Nexon EV",
                    type = "Electric SUV",
                    price = 2800.0,
                    rating = 4.5f,
                    imageResource = R.drawable.ic_car,
                    description = "Electric compact SUV",
                    features = listOf("AC", "Music", "GPS", "Electric", "Fast Charging"),
                    transmission = "Automatic"
                )
            )
        )

        // Initialize displayed cars with all cars
        displayedCars.clear()
        displayedCars.addAll(allCars)
        carAdapter.notifyDataSetChanged()
    }
}

