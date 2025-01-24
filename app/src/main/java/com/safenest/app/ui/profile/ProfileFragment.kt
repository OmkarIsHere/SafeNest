package com.safenest.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.safenest.app.constant.AppConstant
import com.safenest.app.databinding.FragmentProfileBinding
import com.safenest.app.ui.AuthenticationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModel()

    private lateinit var userIcon : ImageView
    private lateinit var userName : TextView
    private lateinit var userEmail : TextView
    private lateinit var userPhone : TextView
    private lateinit var logout : ImageView

    private var uIcon = ""
    private var uName = ""
    private var uEmail = ""
    private var uPhone = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        loadUserData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            userIcon = iconUser
            userName = txtUserName
            userEmail = txtUserEmail
            userPhone = txtUserPhone
            logout = icLogout
        }

        Glide.with(requireActivity()).load(uIcon).into(userIcon)
        userName.text = uName
        userEmail.text = uEmail
        userPhone.text = uPhone

        logout.setOnClickListener {
            profileViewModel.logout()
            val intent = Intent(requireActivity(), AuthenticationActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun loadUserData(){
        uIcon = profileViewModel.getDataFromPreference(AppConstant.userIcon,"")
        Toast.makeText(requireActivity(), uIcon, Toast.LENGTH_SHORT).show()

        val firstName = profileViewModel.getDataFromPreference(AppConstant.userFirstName,"")
        val lastName = profileViewModel.getDataFromPreference(AppConstant.userLastName,"")
        uName = "$firstName $lastName"
        uEmail = profileViewModel.getDataFromPreference(AppConstant.userEmail,"")
        uPhone = profileViewModel.getDataFromPreference(AppConstant.userPhone,"")
    }
}