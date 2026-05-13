package com.example.financeapp.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.financeapp.data.repository.AuthRepository
import com.example.financeapp.databinding.ActivitySplashBinding
import com.example.financeapp.presentation.auth.LoginActivity
import com.example.financeapp.presentation.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            val dest = if (authRepository.isLoggedIn()) MainActivity::class.java
            else LoginActivity::class.java
            startActivity(Intent(this, dest))
            finish()
        }, 1500)
    }
}