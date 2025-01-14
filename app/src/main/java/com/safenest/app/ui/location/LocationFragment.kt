package com.safenest.app.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.safenest.app.databinding.FragmentLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private val locationViewModel: LocationViewModel by activityViewModel<LocationViewModel>()

    private lateinit var error : TextView
    private lateinit var map : MapView
    private var googleMap: GoogleMap? = null

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
        with(binding){
            error = txtError
            map = mapView
        }
        map.onCreate(savedInstanceState)
        map.getMapAsync(this)

        locationViewModel.error.observe(viewLifecycleOwner){ value ->
            if(value.isNotEmpty()){
                error.visibility = View.VISIBLE
                error.text = value
            }else{
                error.visibility = View.GONE
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