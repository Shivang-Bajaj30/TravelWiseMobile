package com.example.travelwise

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.travelwise.data.FirebaseUserRepository
import com.example.travelwise.data.UserRepository
import com.example.travelwise.databinding.ActivityLoginBinding
import com.example.travelwise.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar
        supportActionBar?.hide()

        // Initialize repository
        userRepository = FirebaseUserRepository()

        // Back button click listener
        binding.btnBack.setOnClickListener {
            finish()
        }

        // ✅ Login button click listener (updated)
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                setLoading(true)
                userRepository.login(email, password).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                        if (uid == null) {
                            setLoading(false)
                            Toast.makeText(this, "Login failed. Try again.", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                        userRepository.getUserProfile(uid).addOnCompleteListener { userTask ->
                            setLoading(false)
                            val profile = userTask.result
                            val username = when {
                                profile?.fullName?.isNotBlank() == true -> profile.fullName.substringBefore(" ")
                                else -> email.substringBefore('@')
                            }
                            val prefs = getSharedPreferences("TravelWisePrefs", MODE_PRIVATE)
                            prefs.edit()
                                .putString("USERNAME", username)
                                .putString("EMAIL", email)
                                .putBoolean("LOGGED_IN", true)
                                .apply()

                            val intent = Intent(this, HomeActivity::class.java).apply {
                                putExtra("USERNAME", username)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener { error ->
                            setLoading(false)
                            Toast.makeText(this, error.localizedMessage ?: "Failed to load profile.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        setLoading(false)
                        val ex = authTask.exception
                        when {
                            ex is com.google.firebase.auth.FirebaseAuthInvalidUserException -> {
                                // User account does not exist
                                showNoAccountDialog(email)
                            }
                            ex is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
                                // Wrong password
                                Toast.makeText(this, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show()
                                binding.etPassword.requestFocus()
                                binding.etPassword.text?.clear()
                            }
                            else -> {
                                // Generic error
                                Toast.makeText(this, ex?.localizedMessage ?: "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
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

    private fun setLoading(loading: Boolean) {
        if (isLoading == loading) return
        isLoading = loading
        binding.btnLogin.isEnabled = !loading
        binding.etEmail.isEnabled = !loading
        binding.etPassword.isEnabled = !loading
        binding.progressLogin.visibility = if (loading) View.VISIBLE else View.GONE
        if (loading) {
            binding.btnLogin.text = "Logging in..."
            binding.btnLogin.icon = null
        } else {
            binding.btnLogin.text = "Login"
            binding.btnLogin.icon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_forward)
        }
    }
}

