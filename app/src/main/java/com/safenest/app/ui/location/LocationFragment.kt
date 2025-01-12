package com.safenest.app.ui.location

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.safenest.app.databinding.FragmentLocationBinding
import android.Manifest
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.safenest.app.service.LocationService

class LocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var map : MapView
    private var googleMap: GoogleMap? = null

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
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
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
            map = binding.mapView
        }
            map.onCreate(savedInstanceState)
            map.getMapAsync(this)

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
                ActivityCompat.requestPermissions(requireActivity(), permissions, 101)
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
            if (!hasPermissions()) {
                Toast.makeText(requireActivity(), "Permissions denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        val defaultLocation = LatLng(-34.0, 151.0)
        val location1 = LatLng(-33.91, 151.03)
        val location2 = LatLng(-34.05, 151.15)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        googleMap?.addMarker(MarkerOptions().position(defaultLocation).title("Marker in Sydney"))
        googleMap?.addMarker(MarkerOptions().position(location1).title("Marker in Bankstown"))
        googleMap?.addMarker(MarkerOptions().position(location2).title("Marker in Cronulla"))
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map.onSaveInstanceState(outState)
    }

}