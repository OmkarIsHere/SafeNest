package com.safenest.app.ui.authentication.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.safenest.app.constant.AppConstant
import com.safenest.app.databinding.FragmentLoginBinding
import com.safenest.app.ui.MainActivity
import com.safenest.app.ui.NestActivity
import com.safenest.app.util.Extension
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val loginViewModel: LoginViewModel by viewModel()
    private val binding get() = _binding!!

    private lateinit var email : TextInputEditText
    private lateinit var password : TextInputEditText
    private lateinit var login : Button
    private lateinit var loader : ProgressBar
    private lateinit var loginView : ConstraintLayout

    private lateinit var nestId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var intent: Intent
        getSharedPrefData()
        loginViewModel.userId.observe(viewLifecycleOwner) { value ->
            if(value.isNotEmpty()) {
                if(nestId.isNotEmpty()){
                    intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }else{
                    intent = Intent(requireActivity(), NestActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            email = edtEmail
            password = edtPassword
            login = btnLogin
            loader = progressBar
            loginView = viewLogin
        }

        loginViewModel.authStatus.observe(viewLifecycleOwner) { authState ->
            when (authState) {
                is AuthState.Success -> {
                    getSharedPrefData()
                    Toast.makeText(context, authState.successMessage, Toast.LENGTH_SHORT).show()
                    redirectToHomeScreen()
                    loader.visibility = View.GONE
                    loginView.visibility = View.VISIBLE
                }
                is AuthState.Failure -> {
                    loader.visibility = View.GONE
                    loginView.visibility = View.VISIBLE
                    Toast.makeText(context, authState.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }

        login.setOnClickListener {
            loginView.visibility = View.GONE
            loader.visibility = View.VISIBLE
            loginViewModel.login(
                Extension.trimString(email.text.toString()),
                Extension.trimString(password.text.toString())
            )
        }
    }

    private fun getSharedPrefData(){
        loginViewModel.getDataFromPreference(AppConstant.userId, "")
        nestId = loginViewModel.getDataFromPreference(AppConstant.userNest, "")
    }

    private fun redirectToHomeScreen(){
        val intent : Intent = if(nestId.isNotEmpty())
            Intent(requireActivity(), MainActivity::class.java)
        else
            Intent(activity, NestActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

}