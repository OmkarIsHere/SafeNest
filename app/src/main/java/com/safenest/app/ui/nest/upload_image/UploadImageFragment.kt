package com.safenest.app.ui.nest.upload_image

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
import com.bumptech.glide.Glide
import com.safenest.app.constant.IconsDialog
import com.safenest.app.databinding.FragmentUploadImageBinding
import com.safenest.app.model.ResultState
import com.safenest.app.ui.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class UploadImageFragment : Fragment() {

    private var _binding: FragmentUploadImageBinding? = null
    private val binding get() = _binding!!
    private val uploadImageViewModel: UploadImageViewModel by viewModel()

    private lateinit var uploadImg : Button
    private lateinit var skip : TextView
    private lateinit var pinImg : ImageView
    private lateinit var icon : ImageView
    private lateinit var uploadImage : Button
    private lateinit var loader: ProgressBar

    private var imageUrl = ""

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
            pinImg = locationPin
            icon = userIcon
            uploadImage = btnUploadImage
            loader = progressBar
        }

        uploadImageViewModel.resultState.observe(viewLifecycleOwner) { resultState ->
            loader.visibility = View.GONE
            uploadImage.visibility = View.VISIBLE
            when (resultState) {
                is ResultState.Success -> {
                    redirectToHomeScreen()
                    Toast.makeText(context, resultState.successMessage, Toast.LENGTH_SHORT).show()
                }
                is ResultState.Failure -> {
                    Toast.makeText(context, resultState.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        skip.setOnClickListener {
            redirectToHomeScreen()
        }

        pinImg.setOnClickListener {
            val dialog = IconsDialog(requireActivity())
            dialog.showDialog(
                onImageClick = { imgUrl ->
                    imageUrl = imgUrl
                    Glide.with(requireActivity()).load(imgUrl).into(icon)
                    icon.visibility = View.VISIBLE
                    dialog.dismissDialog()
                }
            )
        }

        uploadImage.setOnClickListener {
            uploadImage.visibility = View.GONE
            loader.visibility = View.VISIBLE
            uploadImageViewModel.updateUserIcon(imageUrl)
        }

    }

    private fun redirectToHomeScreen(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}