package com.safenest.app.ui.nest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.safenest.app.R
import com.safenest.app.databinding.FragmentCreateNestBinding
import com.safenest.app.util.Extension
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateNestFragment : Fragment() {

    private var _binding: FragmentCreateNestBinding? = null
    private val binding get() = _binding!!
    private val nestViewModel: NestViewModel by activityViewModel()

    private lateinit var nestName : TextInputEditText
    private lateinit var back : ImageView
    private  lateinit var next : Button
    private lateinit var loader : ProgressBar
    private lateinit var createNestView : ConstraintLayout

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
            nestName = edtNestName
            back = btnBack
            next = btnCreateNest
            loader = progressBar
            createNestView = viewCreateNest
        }

        nestViewModel.nestState.observe(viewLifecycleOwner) { nestState ->
            when (nestState) {
                is NestState.Success -> {
                    Toast.makeText(context, nestState.successMessage, Toast.LENGTH_SHORT).show()
                    loader.visibility = View.GONE
                    createNestView.visibility = View.VISIBLE
                    nestName.text!!.clear()
                    findNavController().navigate(R.id.action_createNestFragment_to_shareNestFragment)
                }
                is NestState.Failure -> {
                    Toast.makeText(context, nestState.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        back.setOnClickListener {
            findNavController().popBackStack();
        }

        next.setOnClickListener {
            createNestView.visibility = View.GONE
            loader.visibility = View.VISIBLE
            nestViewModel.createNest(Extension.trimString(nestName.text.toString()))
        }
    }

}