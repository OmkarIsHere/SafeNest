package com.safenest.app.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.safenest.app.constant.AppConstant
import com.safenest.app.databinding.FragmentProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModel()

    private lateinit var userName : TextView
    private lateinit var userEmail : TextView
    private lateinit var userPhone : TextView

    private lateinit var setData : Button
    private lateinit var getData : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            userName = txtUserName
            userEmail = txtUserEmail
            userPhone = txtUserPhone
            setData = btnSet
            getData = btnGet
        }
        val firstName = profileViewModel.getDataFromPreference(AppConstant.userFirstName,"")
        val lastName = profileViewModel.getDataFromPreference(AppConstant.userLastName,"")
        userName.text = "$firstName $lastName"
        userEmail.text = profileViewModel.getDataFromPreference(AppConstant.userEmail,"")
        userPhone.text = profileViewModel.getDataFromPreference(AppConstant.userPhone,"")

    }
}