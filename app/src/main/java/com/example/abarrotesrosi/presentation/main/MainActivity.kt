package com.example.abarrotesrosi.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.abarrotesrosi.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


//TODO: Como buenas practicas solo debemos tener un Activity y todo lo demas sean fragmentos
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

    }
}
