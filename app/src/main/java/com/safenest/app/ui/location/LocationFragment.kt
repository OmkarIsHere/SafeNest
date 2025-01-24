package com.safenest.app.ui.location

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.safenest.app.R
import com.safenest.app.databinding.FragmentLocationBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

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
        locationViewModel.readDatabase()
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
        var latLng = LatLng(0.0,0.0)
        var isCameraSet = false

        locationViewModel.members.observe(viewLifecycleOwner){ member ->
            if(member.isNotEmpty()){
                for(m in member){
                    val str = m.userLatLng!!.split(",")
                    latLng = LatLng(str.first().toDouble(), str.last().toDouble())
                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(m.userName)
                            .snippet(m.dateTime)
                            .icon(getBitmapDescriptorFromVector(requireContext(), R.drawable.icon))
                    )
                }
                if(!isCameraSet) {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    isCameraSet = true
                }
            }
        }
    }

    private fun getBitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
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