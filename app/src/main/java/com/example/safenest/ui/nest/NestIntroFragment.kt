package com.example.safenest.ui.nest

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.safenest.R
import com.example.safenest.databinding.FragmentNestIntroBinding
import com.example.safenest.databinding.FragmentSignupBinding
import com.example.safenest.ui.authentication.signup.SignupViewModel

class NestIntroFragment : Fragment() {

    private var _binding: FragmentNestIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNestIntroBinding.inflate(inflater, container, false)
        return binding.root
    }


}