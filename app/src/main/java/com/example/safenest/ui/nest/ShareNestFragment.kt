package com.example.safenest.ui.nest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.safenest.R
import com.example.safenest.databinding.FragmentJoinNestBinding
import com.example.safenest.databinding.FragmentShareNestBinding

class ShareNestFragment : Fragment() {

    private var _binding: FragmentShareNestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareNestBinding.inflate(inflater, container, false)
        return binding.root
    }

}