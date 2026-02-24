package com.example.abarrotesrosi.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.abarrotesrosi.databinding.ActivityMainBinding

//TODO: Como buenas practicas solo debemos tener un Activity y todo lo demas sean fragmentos
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

    }
}
