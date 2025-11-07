package com.example.travelwise

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.travelwise.database.DatabaseHelper
import com.example.travelwise.databinding.ActivitySplashBinding
import com.example.travelwise.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {

//        val signup : Button = findViewById<Button>(R.id.btnSignUp)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar for splash screen
        supportActionBar?.hide()

        // Initialize database to ensure it's created (for Database Inspector)
        DatabaseHelper(this).readableDatabase.close()

        binding.btnExplore.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}
