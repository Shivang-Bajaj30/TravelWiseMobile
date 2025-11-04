package com.example.travelwise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelwise.adapters.MealAdapter
import com.example.travelwise.databinding.ActivityMealBinding
import com.example.travelwise.models.Meal

class MealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealBinding
    private lateinit var mealAdapter: MealAdapter
    private val allMeals = mutableListOf<Meal>()
    private val displayedMeals = mutableListOf<Meal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
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
        loadSampleMeals()
        setupSearch()
    }

    private fun setupRecyclerView() {
        mealAdapter = MealAdapter(displayedMeals) { meal ->
            Toast.makeText(this, "${meal.name} selected", Toast.LENGTH_SHORT).show()
        }

        binding.rvMeals.apply {
            layoutManager = LinearLayoutManager(this@MealActivity)
            adapter = mealAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterMeals(s.toString())
            }
        })
    }

    private fun filterMeals(query: String) {
        val searchQuery = query.trim().lowercase()
        
        displayedMeals.clear()
        
        if (searchQuery.isEmpty()) {
            displayedMeals.addAll(allMeals)
        } else {
            displayedMeals.addAll(
                allMeals.filter { meal ->
                    meal.name.lowercase().contains(searchQuery) ||
                    meal.restaurant.lowercase().contains(searchQuery) ||
                    meal.cuisine.lowercase().contains(searchQuery)
                }
            )
        }
        
        mealAdapter.notifyDataSetChanged()
    }

    private fun loadSampleMeals() {
        allMeals.clear()
        allMeals.addAll(
            listOf(
                Meal(
                    id = 1,
                    name = "Butter Chicken",
                    restaurant = "Karim's",
                    cuisine = "Mughlai",
                    price = 350.0,
                    rating = 4.8f,
                    imageResource = R.drawable.ic_meal,
                    description = "Creamy tomato-based curry",
                    location = "Delhi"
                ),
                Meal(
                    id = 2,
                    name = "Biryani",
                    restaurant = "Paradise",
                    cuisine = "Hyderabadi",
                    price = 450.0,
                    rating = 4.9f,
                    imageResource = R.drawable.ic_meal,
                    description = "Fragrant spiced rice with meat",
                    location = "Hyderabad"
                ),
                Meal(
                    id = 3,
                    name = "Masala Dosa",
                    restaurant = "MTR",
                    cuisine = "South Indian",
                    price = 120.0,
                    rating = 4.7f,
                    imageResource = R.drawable.ic_meal,
                    description = "Crispy rice crepe with spiced potatoes",
                    location = "Bangalore"
                ),
                Meal(
                    id = 4,
                    name = "Pav Bhaji",
                    restaurant = "Cafe Madras",
                    cuisine = "Mumbai Street Food",
                    price = 150.0,
                    rating = 4.6f,
                    imageResource = R.drawable.ic_meal,
                    description = "Spiced vegetable curry with buttered buns",
                    location = "Mumbai"
                ),
                Meal(
                    id = 5,
                    name = "Chole Bhature",
                    restaurant = "Sita Ram",
                    cuisine = "North Indian",
                    price = 180.0,
                    rating = 4.5f,
                    imageResource = R.drawable.ic_meal,
                    description = "Spiced chickpeas with fluffy bread",
                    location = "Delhi"
                ),
                Meal(
                    id = 6,
                    name = "Fish Curry",
                    restaurant = "Fisherman's Wharf",
                    cuisine = "Goan",
                    price = 550.0,
                    rating = 4.8f,
                    imageResource = R.drawable.ic_meal,
                    description = "Coconut-based spicy fish curry",
                    location = "Goa"
                ),
                Meal(
                    id = 7,
                    name = "Rogan Josh",
                    restaurant = "Shamyana",
                    cuisine = "Kashmiri",
                    price = 650.0,
                    rating = 4.7f,
                    imageResource = R.drawable.ic_meal,
                    description = "Aromatic lamb curry",
                    location = "Srinagar"
                ),
                Meal(
                    id = 8,
                    name = "Thali",
                    restaurant = "Rajdhani",
                    cuisine = "Gujarati/Rajasthani",
                    price = 400.0,
                    rating = 4.6f,
                    imageResource = R.drawable.ic_meal,
                    description = "Complete meal with multiple dishes",
                    location = "Mumbai"
                ),
                Meal(
                    id = 9,
                    name = "Pani Puri",
                    restaurant = "Elco",
                    cuisine = "Street Food",
                    price = 80.0,
                    rating = 4.9f,
                    imageResource = R.drawable.ic_meal,
                    description = "Crispy puris with tangy water",
                    location = "Mumbai"
                ),
                Meal(
                    id = 10,
                    name = "Vada Pav",
                    restaurant = "Ashok Vada Pav",
                    cuisine = "Mumbai Street Food",
                    price = 30.0,
                    rating = 4.8f,
                    imageResource = R.drawable.ic_meal,
                    description = "Spiced potato fritter in bun",
                    location = "Mumbai"
                )
            )
        )

        // Initialize displayed meals with all meals
        displayedMeals.clear()
        displayedMeals.addAll(allMeals)
        mealAdapter.notifyDataSetChanged()
    }
}

