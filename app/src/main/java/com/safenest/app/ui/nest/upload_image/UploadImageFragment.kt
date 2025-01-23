package com.safenest.app.ui.nest.upload_image

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.safenest.app.R
import com.safenest.app.databinding.FragmentShareNestBinding
import com.safenest.app.databinding.FragmentUploadImageBinding
import com.safenest.app.ui.MainActivity

class UploadImageFragment : Fragment() {

    private var _binding: FragmentUploadImageBinding? = null
    private val binding get() = _binding!!
    private val uploadImageViewModel: UploadImageViewModel by viewModels()

    private lateinit var uploadImg : Button
    private lateinit var skip : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            uploadImg = btnUploadImage
            skip = txtSkip
        }

        skip.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}