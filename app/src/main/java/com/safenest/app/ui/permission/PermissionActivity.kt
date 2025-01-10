package com.safenest.app.ui.permission

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.safenest.app.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}