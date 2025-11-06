package com.example.travelwise

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.travelwise.databinding.ActivityAiTripPlanBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

class AiTripPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiTripPlanBinding

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val startCal: Calendar = Calendar.getInstance()
    private val endCal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiTripPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val destination = intent.getStringExtra("DESTINATION_QUERY") ?: ""
        binding.etDestination.setText(destination)

        setupDatePickers()
        setupActions()
    }

    private fun setupDatePickers() {
        binding.etStartDate.setOnClickListener {
            val cal = startCal
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    binding.etStartDate.setText(dateFormatter.format(cal.time))
                    // Ensure end date is not before start date
                    if (endCal.timeInMillis < cal.timeInMillis) {
                        endCal.timeInMillis = cal.timeInMillis
                        binding.etEndDate.setText(dateFormatter.format(endCal.time))
                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.etEndDate.setOnClickListener {
            val cal = endCal
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    // prevent end before start
                    if (cal.timeInMillis < startCal.timeInMillis) {
                        Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show()
                        return@DatePickerDialog
                    }
                    binding.etEndDate.setText(dateFormatter.format(cal.time))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupActions() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        val generativeModel = GenerativeModel(
            modelName = BuildConfig.GEMINI_MODEL,
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            }
        )

        binding.btnGenerate.setOnClickListener {
            val dest = binding.etDestination.text.toString().trim()
            val budget = binding.etBudget.text.toString().trim()
            val people = binding.etPeople.text.toString().trim()
            val start = binding.etStartDate.text.toString().trim()
            val end = binding.etEndDate.text.toString().trim()
            val notes = binding.etAdditionalInfo.text.toString().trim()

            if (!validateInputs(dest, budget, people, start, end)) {
                return@setOnClickListener
            }

            val prompt = buildPrompt(dest, budget, people, start, end, notes)

            // UI state
            binding.btnGenerate.isEnabled = false
            binding.btnGenerate.text = "Generating..."
            binding.tvOutput.visibility = View.VISIBLE
            binding.tvOutput.text = "Generating structured trip plan..."

            lifecycleScope.launch {
                try {
                    val response = generativeModel.generateContent(prompt)
                    val result = response.text

                    if (result == null) {
                        handleGenerationFailure("Empty response from AI.")
                        return@launch
                    }

                    // Navigate to results page with generated JSON itinerary
                    val intent = Intent(this@AiTripPlanActivity, ItineraryResultsActivity::class.java).apply {
                        putExtra("DESTINATION", dest)
                        putExtra("START_DATE", start)
                        putExtra("END_DATE", end)
                        putExtra("PEOPLE_COUNT", people)
                        putExtra("ITINERARY_TEXT", result)
                    }
                    startActivity(intent)
                    finish() // Close the form activity
                } catch (e: Exception) {
                    Log.e("AiTripPlan", "Error generating plan", e)
                    handleGenerationFailure("Failed to generate plan: ${e.message}")
                }
            }
        }
    }
    
    private fun validateInputs(dest: String, budget: String, people: String, start: String, end: String): Boolean {
        if (dest.isEmpty()) {
            binding.etDestination.error = "Destination is required"
            return false
        }
        if (budget.isEmpty()) {
            binding.etBudget.error = "Budget is required"
            return false
        }
        if (people.isEmpty()) {
            binding.etPeople.error = "Number of people is required"
            return false
        }
        if (start.isEmpty()) {
            binding.etStartDate.error = "Start date is required"
            return false
        }
        if (end.isEmpty()) {
            binding.etEndDate.error = "End date is required"
            return false
        }
        return true
    }
    
    private fun handleGenerationFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        binding.btnGenerate.isEnabled = true
        binding.btnGenerate.text = "Generate Plan"
        binding.tvOutput.text = message
    }

    private fun buildPrompt(
        destination: String,
        budget: String,
        peopleCount: String,
        startDate: String,
        endDate: String,
        notes: String
    ): String {
        return """
        You are an expert travel planner. Generate a detailed, day-by-day itinerary based on the following details.
        Your response MUST be a single, valid JSON object and nothing else. Do not include any text or formatting like ```json before or after the JSON.

        Destination: $destination
        Travel Dates: $startDate to $endDate
        Number of People: $peopleCount
        Budget: $budget
        Additional Preferences: $notes

        Structure your response using the following JSON format:
        {
          "itinerary": [
            {
              "day": 1,
              "date": "$startDate",
              "activities": [
                {
                  "time": "Morning",
                  "title": "Arrival and Hotel Check-in",
                  "description": "Arrive at the destination, transfer to your hotel, and check in.",
                  "type": "HOTEL"
                },
                {
                  "time": "Afternoon",
                  "title": "Explore the Local Market",
                  "description": "Visit a local market for some initial sightseeing and to get a feel for the city.",
                  "type": "ATTRACTION"
                }
              ]
            }
          ]
        }

        - The root object must contain a single key: `itinerary`, which is an array of day objects.
        - Each object in the `itinerary` array must have a `day` number, a `date` string, and an `activities` array.
        - Each object in the `activities` array must have a `time`, `title`, `description`, and a `type`.
        - The `type` field must be one of the following exact strings: FLIGHT, HOTEL, MEAL, ATTRACTION, TRANSPORT, GENERAL.
        - Ensure the dates for each day are correctly incremented starting from the provided start date.
        - Provide a complete plan for the entire duration of the trip.
        """.trimIndent()
    }
}
