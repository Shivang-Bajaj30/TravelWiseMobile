package com.example.travelwise

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelwise.databinding.ActivityLoginBinding
import com.example.travelwise.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Back button click listener
        binding.btnBack.setOnClickListener {
            finish()
        }

        // ✅ Login button click listener (updated)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                // ✅ Extract username (first part of email)
                val username = email.substringBefore("@")

                // ✅ Start HomeActivity and pass username
                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra("USERNAME", username)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
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
}

