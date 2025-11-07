package com.example.travelwise

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.travelwise.database.DatabaseHelper
import com.example.travelwise.databinding.ActivityLoginBinding
import com.example.travelwise.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)
        
        // Initialize database for Database Inspector
        databaseHelper.initializeDatabase()

        // Debug: Print all users to Logcat (remove in production)
        databaseHelper.printAllUsers()

        // Back button click listener
        binding.btnBack.setOnClickListener {
            finish()
        }

        // ✅ Login button click listener (updated)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                // First check if user exists in database
                if (!databaseHelper.userExists(email)) {
                    // User doesn't exist - show popup to create account
                    showNoAccountDialog(email)
                } else {
                    // User exists - authenticate with password
                    val user = databaseHelper.authenticateUser(email, password)
                    
                    if (user != null) {
                        // Login successful
                        // ✅ Extract username (first part of full name or email)
                        val username = user.fullName.substringBefore(" ")

                        // ✅ Persist session
                        val prefs = getSharedPreferences("TravelWisePrefs", MODE_PRIVATE)
                        prefs.edit()
                            .putString("USERNAME", username)
                            .putString("EMAIL", email)
                            .putBoolean("LOGGED_IN", true)
                            .apply()

                        // ✅ Start HomeActivity and pass username
                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra("USERNAME", username)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        // User exists but password is wrong
                        Toast.makeText(this, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show()
                        binding.etPassword.requestFocus()
                        binding.etPassword.text?.clear()
                    }
                }
            }
        }

        // Forgot password click listener
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
            // TODO: Implement forgot password functionality
        }

        // Sign up link click listener
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Google login click listener
        binding.btnGoogleLogin.setOnClickListener {
            Toast.makeText(this, "Google Login clicked", Toast.LENGTH_SHORT).show()
            // TODO: Implement Google login
        }

        // Facebook login click listener
        binding.btnFacebookLogin.setOnClickListener {
            Toast.makeText(this, "Facebook Login clicked", Toast.LENGTH_SHORT).show()
            // TODO: Implement Facebook login
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter a valid email"
            binding.etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            binding.etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            binding.etPassword.requestFocus()
            return false
        }

        return true
    }

    /**
     * Show dialog when user account doesn't exist
     */
    private fun showNoAccountDialog(email: String) {
        if (!isFinishing && !isDestroyed) {
            AlertDialog.Builder(this)
                .setTitle("Account Not Found")
                .setMessage("No account found with email: $email\n\nPlease create an account to continue.")
                .setPositiveButton("Create Account") { _, _ ->
                    // Navigate to SignupActivity
                    val intent = Intent(this, SignupActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(true)
                .show()
        }
    }
}

