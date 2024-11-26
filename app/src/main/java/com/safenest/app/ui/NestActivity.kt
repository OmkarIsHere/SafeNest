package com.safenest.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.safenest.app.databinding.ActivityNestBinding

class NestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNestBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}