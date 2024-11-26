package com.safenest.app.ui.nest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.safenest.app.R
import com.safenest.app.databinding.FragmentCreateNestBinding

class CreateNestFragment : Fragment() {

    private var _binding: FragmentCreateNestBinding? = null
    private val binding get() = _binding!!

    private lateinit var back : ImageView
    private  lateinit var next : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            back = btnBack
            next = btnCreateNest
        }

        back.setOnClickListener {
            findNavController().popBackStack();
        }

        next.setOnClickListener {
            findNavController().navigate(R.id.action_createNestFragment_to_shareNestFragment)
        }
    }

}