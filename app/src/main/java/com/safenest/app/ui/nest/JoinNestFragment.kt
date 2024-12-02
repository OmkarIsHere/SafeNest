package com.safenest.app.ui.nest

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.safenest.app.R
import com.safenest.app.databinding.FragmentJoinNestBinding
import com.safenest.app.ui.MainActivity
import com.safenest.app.util.Extension
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class JoinNestFragment : Fragment() {

    private var _binding: FragmentJoinNestBinding? = null
    private val binding get() = _binding!!
    private val nestViewModel: NestViewModel by activityViewModel()

    private lateinit var back: ImageView
    private lateinit var nestCode : TextInputEditText
    private lateinit var joinNest : Button
    private lateinit var createNest: TextView
    private lateinit var loader : ProgressBar
    private lateinit var joinNestView : ConstraintLayout

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
            joinNest = btnJoinNest
            nestCode = edtNestCode
            loader = progressBar
            joinNestView = viewJoinNest
        }

        nestViewModel.nestState.observe(viewLifecycleOwner) { nestState ->
            when (nestState) {
                is NestState.Success -> {
                    Toast.makeText(context, nestState.successMessage, Toast.LENGTH_SHORT).show()
                    loader.visibility = View.GONE
                    joinNestView.visibility = View.VISIBLE
                    nestCode.text!!.clear()
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                is NestState.Failure -> {
                    Toast.makeText(context, nestState.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        back.setOnClickListener {
            findNavController().popBackStack();
        }

        joinNest.setOnClickListener {
            joinNestView.visibility = View.GONE
            loader.visibility = View.VISIBLE
            nestViewModel.joinNest(Extension.trimString(nestCode.text.toString()))
        }

        createNest.setOnClickListener {
            findNavController().navigate(R.id.action_joinNestFragment_to_createNestFragment)
        }
    }

}