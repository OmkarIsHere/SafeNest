package com.safenest.app.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.safenest.app.databinding.FragmentHomeBinding
import android.Manifest
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.safenest.app.service.LocationService

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var startService : Button
        var stopService : Button
        var requestPermissions : Button
        with(binding){
            startService = btnStartService
            stopService = btnStopService
            requestPermissions = btnRequestPermissions
        }


        // Check permissions
        if (!hasPermissions()) {
            requestPermissions.visibility = Button.VISIBLE
        } else {
            requestPermissions.visibility = Button.GONE
        }

        // Start Service
        startService.setOnClickListener {
            if (hasPermissions()) {
                val intent = Intent(requireActivity(), LocationService::class.java)
                requireActivity().startService(intent)
                Log.d("LOCATION_SERVICE", "HomeFragment : Service started")
            } else {
                Toast.makeText(requireActivity(), "Permissions not granted!", Toast.LENGTH_SHORT).show()
            }
        }

        stopService.setOnClickListener {
            val intent = Intent(requireActivity(), LocationService::class.java)
            requireActivity().stopService(intent)
        }

        // Request Permissions
        requestPermissions.setOnClickListener {
            ActivityCompat.requestPermissions(requireActivity(), permissions, 101)
        }
    }

    private fun hasPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(requireActivity(), it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (hasPermissions()) {
                Toast.makeText(requireActivity(), "Permissions granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}