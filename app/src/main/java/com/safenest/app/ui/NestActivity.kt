package com.safenest.app.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.safenest.app.databinding.ActivityNestBinding
import com.safenest.app.ui.nest.NestViewModel

class NestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNestBinding
    private val nestViewModel: NestViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNestBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}