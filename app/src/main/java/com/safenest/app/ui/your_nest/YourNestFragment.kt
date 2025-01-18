package com.safenest.app.ui.your_nest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.safenest.app.R
import com.safenest.app.adapters.MemberAdapter
import com.safenest.app.databinding.FragmentYourNestBinding
import com.safenest.app.model.Member
import com.safenest.app.model.ResultState
import com.safenest.app.ui.location.LocationViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class YourNestFragment : Fragment() {

    private var _binding: FragmentYourNestBinding? = null
    private val binding get() = _binding!!
    private val yourNestViewModel: YourNestViewModel by viewModel()
    private val locationViewModel: LocationViewModel by activityViewModel<LocationViewModel>()

    private lateinit var nestName : TextView
    private lateinit var nestMembers : RecyclerView
    private lateinit var loader : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        yourNestViewModel.getNestData()
        _binding = FragmentYourNestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            nestName = txtNestName
            nestMembers = recyclerViewNestMembers
            loader = progressBar
        }
        loader.visibility = View.VISIBLE

        yourNestViewModel.resultState.observe(viewLifecycleOwner) { resultState ->
            when (resultState) {
                is ResultState.Success -> {
                    loader.visibility = View.GONE
                }
                is ResultState.Failure -> {
                    Toast.makeText(context, resultState.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        yourNestViewModel.nest.observe(viewLifecycleOwner) { nest ->
            if (nest != null) {
                nestName.text = nest.nestName
            }
        }

        locationViewModel.members.observe(viewLifecycleOwner){ member ->
            if(member.isNotEmpty()){
                setMemberList(ArrayList(member))
            }
        }

    }

    private fun setMemberList(members : ArrayList<Member>){
        nestMembers.layoutManager = LinearLayoutManager(context)
        val recyclerAdapter = MemberAdapter(requireContext(), members)
        nestMembers.adapter = recyclerAdapter
    }
}