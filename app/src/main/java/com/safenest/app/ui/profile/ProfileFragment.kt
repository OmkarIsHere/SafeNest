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
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.safenest.app.constant.AppConstant
import com.safenest.app.constant.CustomDialog
import com.safenest.app.constant.IconsDialog
import com.safenest.app.databinding.FragmentProfileBinding
import com.safenest.app.model.ResultState
import com.safenest.app.ui.AuthenticationActivity
import com.safenest.app.ui.nest.upload_image.UploadImageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModel()
    private val uploadImageViewModel: UploadImageViewModel by viewModel()

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

        setIcon(uIcon)
        userName.text = uName
        userEmail.text = uEmail
        userPhone.text = uPhone

        logout.setOnClickListener {
            val workManager = WorkManager.getInstance(requireContext())
            workManager.getWorkInfosByTagLiveData(AppConstant.DO_WORK).observeForever { workInfoList ->
                val isWorkEnqueued = workInfoList.any { it.state == WorkInfo.State.ENQUEUED }
                if(isWorkEnqueued){
                    Toast.makeText(requireActivity(), "Please stop notifying before logout", Toast.LENGTH_SHORT).show()
                }else{
                    val dialog = CustomDialog(requireActivity())
                    dialog.showDialog(
                        title = "Log out",
                        message = "Are you sure, you want to logout?",
                        positiveButtonText = "LOGOUT",
                        negativeButtonText = "Cancel",
                        onPositiveClick = { logout() }
                    )
                }
            }
        }

        userIcon.setOnClickListener {
            val dialog = IconsDialog(requireActivity())
            dialog.showDialog(
                onImageClick = { imgUrl ->
                    setIcon(imgUrl)
                    uploadImageViewModel.updateUserIcon(imgUrl)
                    dialog.dismissDialog()
                    uploadImageViewModel.resultState.observe(viewLifecycleOwner) { resultState ->
                        when (resultState) {
                            is ResultState.Success -> {
                                Toast.makeText(context, resultState.successMessage, Toast.LENGTH_SHORT).show()
                            }
                            is ResultState.Failure -> {
                                Toast.makeText(context, resultState.errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            )
        }
    }

    private fun setIcon(iconUrl: String){
        Glide.with(requireActivity()).load(iconUrl).into(userIcon)
    }

    private fun loadUserData(){
        uIcon = profileViewModel.getDataFromPreference(AppConstant.USER_ICON,"")

        val firstName = profileViewModel.getDataFromPreference(AppConstant.USER_FIRSTNAME,"")
        val lastName = profileViewModel.getDataFromPreference(AppConstant.USER_LASTNAME,"")
        uName = "$firstName $lastName"
        uEmail = profileViewModel.getDataFromPreference(AppConstant.USER_EMAIL,"")
        uPhone = profileViewModel.getDataFromPreference(AppConstant.USER_PHONE,"")
    }

    private fun logout(){
        profileViewModel.logout()
        val intent = Intent(requireActivity(), AuthenticationActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}