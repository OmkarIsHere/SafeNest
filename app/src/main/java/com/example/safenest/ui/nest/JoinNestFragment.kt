package com.example.safenest.ui.nest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.safenest.R
import com.example.safenest.databinding.FragmentJoinNestBinding

class JoinNestFragment : Fragment() {

    private var _binding: FragmentJoinNestBinding? = null
    private val binding get() = _binding!!

    private lateinit var back: ImageView
    private lateinit var createNest: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinNestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            back = btnBack
            createNest = txtCreateNest
        }

        back.setOnClickListener {
            findNavController().popBackStack();
        }

        createNest.setOnClickListener {
            findNavController().navigate(R.id.action_joinNestFragment_to_createNestFragment)
        }
    }

}