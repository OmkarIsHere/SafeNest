package com.safenest.app.ui.nest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.safenest.app.R
import com.safenest.app.databinding.FragmentShareNestBinding
import com.safenest.app.ui.MainActivity
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ShareNestFragment : Fragment() {

    private var _binding: FragmentShareNestBinding? = null
    private val binding get() = _binding!!
    private val nestViewModel: NestViewModel by activityViewModel()

    private lateinit var nestCode : TextView
    private lateinit var share : Button
    private lateinit var skip : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareNestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            nestCode = txtNestCode
            share = btnShareCode
            skip = txtSkip
        }
        var nestId = ""
        nestViewModel.nId.observe(viewLifecycleOwner){ value ->
            Log.w("NestViewModel", "onViewCreated: $value")
            nestId = value
            nestCode.text = nestId
        }

        skip.setOnClickListener {
//            val intent = Intent(requireActivity(), MainActivity::class.java)
//            startActivity(intent)
//            requireActivity().finish()
            findNavController().navigate(R.id.action_shareNestFragment_to_uploadImageFragment)
        }

        share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Hey there,\nThis is my nest code. Join real quick!!\nCode: $nestId")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, "SafeNest")
            startActivity(shareIntent)
        }
    }

}