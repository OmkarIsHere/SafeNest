package com.safenest.app.ui.location

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.safenest.app.R
import com.safenest.app.R.*
import com.safenest.app.databinding.FragmentLocationBinding
import com.safenest.app.model.Member
import com.safenest.app.util.Extension
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class LocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private val locationViewModel: LocationViewModel by activityViewModel<LocationViewModel>()

    private lateinit var error: TextView
    private lateinit var map: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var notify: Button

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
            notify = btnNotify
        }
        map.onCreate(savedInstanceState)
        map.getMapAsync(this)
        locationViewModel.isNotifyWorkerStarted(requireActivity())

        locationViewModel.error.observe(viewLifecycleOwner){ value ->
            if(value.isNotEmpty()){
                error.visibility = View.VISIBLE
                error.text = value
            }else{
                error.visibility = View.GONE
            }
        }

        locationViewModel.isNotifyEnqueue.observe(viewLifecycleOwner){ value ->
            notify.text = if(value) "STOP\nNOTIFY" else "START\nNOTIFY"
        }

        notify.setOnClickListener {
            locationViewModel.notifyWork(requireContext())
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
        val markerMap = HashMap<Marker, Member>()

        locationViewModel.members.observe(viewLifecycleOwner){ member ->
            if(member.isNotEmpty()){
                markerMap.clear()
                for(m in member){
                    val str = m.userLatLng!!.split(",")
                    latLng = LatLng(str.first().toDouble(), str.last().toDouble())

                    customMarker(requireContext(), m.userIcon!!) { bitmapDescriptor ->
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .icon(bitmapDescriptor)
                        )

                        marker?.let {
                            markerMap[it] = m
                        }
                    }
                }
                if(!isCameraSet) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    isCameraSet = true
                }
            }
        }

        googleMap.setOnMarkerClickListener { marker ->
            markerMap[marker]?.let { member ->
                showBottomSheetDialog(requireContext(), member)
            }
            true
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

    private fun customMarker(
        context: Context,
        imageUrl: String,
        callback: (BitmapDescriptor) -> Unit
    ) {
        val markerView = LayoutInflater.from(context).inflate(layout.custom_marker, null)
        val profileImageView = markerView.findViewById<ImageView>(R.id.imgPinIcon)

        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .placeholder(drawable.location_pin)
            .error(drawable.ic_location_24)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    profileImageView.setImageBitmap(resource)

                    markerView.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)

                    val bitmap = Bitmap.createBitmap(
                        markerView.measuredWidth,
                        markerView.measuredHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    markerView.draw(canvas)

                    callback(BitmapDescriptorFactory.fromBitmap(bitmap))
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun showBottomSheetDialog(context: Context, member:Member) {
        val dialog = BottomSheetDialog(context)
        val view = layoutInflater.inflate(layout.user_info_bottom_sheet, null)

        val mImg = view.findViewById<ImageView>(R.id.memberIcon)
        val mName = view.findViewById<TextView>(R.id.txtMemberName)
        val mPhone = view.findViewById<TextView>(R.id.txtMemberPhone)
        val dateTime = view.findViewById<TextView>(R.id.txtDateTime)
        val location = view.findViewById<TextView>(R.id.txtMemberLocation)
        val battery = view.findViewById<TextView>(R.id.txtMemberBattery)
        val network = view.findViewById<TextView>(R.id.txtMemberNetwork)
        val call = view.findViewById<ConstraintLayout>(R.id.callView)
        val message = view.findViewById<ConstraintLayout>(R.id.messageView)

        Glide.with(context).load(member.userIcon).into(mImg)
        mName.text = member.userName
        mPhone.text = member.userPhone
        dateTime.text = Extension.convertDateTime(member.dateTime?:"")
        location.text = member.userLatLng
        battery.text = "${member.battery}%"
        network.text = member.internet

        call.setOnClickListener {
            try{
                val phoneIntent = Intent(Intent.ACTION_CALL)
                phoneIntent.data = Uri.parse("tel:${member.userPhone}")
                startActivity(phoneIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Please allow phone call permission from settings.", Toast.LENGTH_SHORT).show()
            }
        }

        message.setOnClickListener {
            try {
                val uri = Uri.parse("https://wa.me/${member.userPhone}?text=${Uri.encode("Hello!!")}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.whatsapp")
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "WhatsApp is not installed on your device.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setContentView(view)
        dialog.show()
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