package com.safenest.app.ui.nest

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.safenest.app.R
import com.safenest.app.databinding.FragmentNestIntroBinding
import com.safenest.app.databinding.FragmentSignupBinding
import com.safenest.app.ui.authentication.signup.SignupViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class NestIntroFragment : Fragment() {

    private var _binding: FragmentNestIntroBinding? = null
    private val binding get() = _binding!!

    private val nestViewModel: NestViewModel by activityViewModel()
    private lateinit var next : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNestIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            next = btnNext
        }

        next.setOnClickListener {
            findNavController().navigate(R.id.action_nestIntroFragment_to_joinNestFragment)
        }
    }
}