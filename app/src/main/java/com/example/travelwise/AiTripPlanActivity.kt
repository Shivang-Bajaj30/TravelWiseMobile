package com.example.travelwise

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.travelwise.databinding.ActivityAiTripPlanBinding
import com.example.travelwise.utils.AiService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

        binding.btnGenerate.setOnClickListener {
            // Placeholder: Later this will trigger AI generation
            val dest = binding.etDestination.text.toString().trim()
            val budget = binding.etBudget.text.toString().trim()
            val people = binding.etPeople.text.toString().trim()
            val start = binding.etStartDate.text.toString().trim()
            val end = binding.etEndDate.text.toString().trim()
            val notes = binding.etAdditionalInfo.text.toString().trim()

            if (dest.isEmpty()) {
                binding.etDestination.error = "Destination is required"
                return@setOnClickListener
            }

            if (people.isNotEmpty() && people.toIntOrNull() == null) {
                binding.etPeople.error = "Enter a valid number"
                return@setOnClickListener
            }

            val prompt = buildPrompt(
                destination = dest,
                budget = budget,
                peopleCount = people,
                startDate = start,
                endDate = end,
                notes = notes
            )

            // UI state
            binding.btnGenerate.isEnabled = false
            binding.btnGenerate.text = "Generating..."
            binding.tvOutput.visibility = View.VISIBLE
            binding.tvOutput.text = "Generating trip plan..."

            lifecycleScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        AiService().generateTripPlan(prompt)
                    }
                    binding.tvOutput.text = result
                } catch (e: Exception) {
                    binding.tvOutput.text = "Failed to generate plan: ${e.message}"
                } finally {
                    binding.btnGenerate.isEnabled = true
                    binding.btnGenerate.text = "Generate Plan"
                }
            }
        }
    }

    private fun buildPrompt(
        destination: String,
        budget: String,
        peopleCount: String,
        startDate: String,
        endDate: String,
        notes: String
    ): String {
        val sb = StringBuilder()
        sb.appendLine("You are a helpful travel planner. Create a concise, day-by-day trip plan.")
        sb.appendLine("Destination: $destination")
        if (budget.isNotBlank()) sb.appendLine("Budget: $budget")
        if (peopleCount.isNotBlank()) sb.appendLine("Number of people: $peopleCount")
        if (startDate.isNotBlank()) sb.appendLine("Start date: $startDate")
        if (endDate.isNotBlank()) sb.appendLine("End date: $endDate")
        if (notes.isNotBlank()) sb.appendLine("Additional preferences: $notes")
        sb.appendLine()
        sb.appendLine("Please include:")
        sb.appendLine("- Recommended daily itinerary with major attractions")
        sb.appendLine("- Suggested hotels in different price tiers")
        sb.appendLine("- Local transport tips and approximate costs")
        sb.appendLine("- Food recommendations (vegetarian options if possible)")
        sb.appendLine("- A summary budget estimate")
        return sb.toString()
    }
}


