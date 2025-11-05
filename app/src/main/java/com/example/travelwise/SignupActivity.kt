package com.example.travelwise

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelwise.databinding.ActivitySignupBinding
import com.example.travelwise.ui.home.HomeActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        setupPasswordToggles()
        setupClickListeners()
    }

    private fun setupPasswordToggles() {
        // Password visibility toggle
        binding.btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.btnTogglePassword.setImageResource(R.drawable.ic_visibility)
            } else {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.btnTogglePassword.setImageResource(R.drawable.ic_visibility_off)
            }
            // Move cursor to end
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        // Confirm Password visibility toggle
        binding.btnToggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            if (isConfirmPasswordVisible) {
                binding.etConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility)
            } else {
                binding.etConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility_off)
            }
            // Move cursor to end
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text?.length ?: 0)
        }
    }

    private fun setupClickListeners() {
        // Back button click listener
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Sign up button click listener
        binding.btnSignUp.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val termsAccepted = binding.cbTerms.isChecked

            if (validateInput(fullName, email, phone, password, confirmPassword, termsAccepted)) {
                // For now, just navigate to HomeActivity
                // In production, you would register the user with a backend
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                // ✅ Persist session
                val username = fullName.substringBefore(" ")
                val prefs = getSharedPreferences("TravelWisePrefs", MODE_PRIVATE)
                prefs.edit()
                    .putString("USERNAME", username)
                    .putString("EMAIL", email)
                    .putBoolean("LOGGED_IN", true)
                    .apply()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        // Terms and Conditions click listener
        binding.tvTerms.setOnClickListener {
            Toast.makeText(this, "Terms & Conditions clicked", Toast.LENGTH_SHORT).show()
            // TODO: Show terms and conditions dialog or navigate to terms page
        }

        // Login link click listener
        binding.tvLogin.setOnClickListener {
            // Navigate back to login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Google sign up click listener
        binding.btnGoogleSignUp.setOnClickListener {
            Toast.makeText(this, "Google Sign Up clicked", Toast.LENGTH_SHORT).show()
            // TODO: Implement Google sign up
        }

        // Facebook sign up click listener
        binding.btnFacebookSignUp.setOnClickListener {
            Toast.makeText(this, "Facebook Sign Up clicked", Toast.LENGTH_SHORT).show()
            // TODO: Implement Facebook sign up
        }
    }

    private fun validateInput(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        termsAccepted: Boolean
    ): Boolean {
        // Validate full name
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Full name is required", Toast.LENGTH_SHORT).show()
            binding.etFullName.requestFocus()
            return false
        }

        if (fullName.length < 3) {
            Toast.makeText(this, "Name must be at least 3 characters", Toast.LENGTH_SHORT).show()
            binding.etFullName.requestFocus()
            return false
        }

        // Validate email
        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            binding.etEmail.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            binding.etEmail.requestFocus()
            return false
        }

        // Validate phone
        if (phone.isEmpty()) {
            Toast.makeText(this, "Phone number is required", Toast.LENGTH_SHORT).show()
            binding.etPhone.requestFocus()
            return false
        }

        if (phone.length < 10) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            binding.etPhone.requestFocus()
            return false
        }

        // Validate password
        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
            binding.etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            binding.etPassword.requestFocus()
            return false
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show()
            binding.etConfirmPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            binding.etConfirmPassword.requestFocus()
            return false
        }

        // Validate terms acceptance
        if (!termsAccepted) {
            Toast.makeText(this, "Please accept the Terms & Conditions", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
