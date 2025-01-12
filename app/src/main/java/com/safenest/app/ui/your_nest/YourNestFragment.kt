package com.safenest.app.ui.your_nest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.safenest.app.databinding.FragmentProfileBinding
import com.safenest.app.databinding.FragmentYourNestBinding

class YourNestFragment : Fragment() {

    private var _binding: FragmentYourNestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYourNestBinding.inflate(inflater, container, false)
        return binding.root
    }
}