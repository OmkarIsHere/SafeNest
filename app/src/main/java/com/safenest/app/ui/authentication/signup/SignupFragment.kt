package com.safenest.app.ui.authentication.signup

import android.content.Intent
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
import com.google.android.material.textfield.TextInputEditText
import com.ozcanalasalvar.otp_view.view.OtpView
import com.safenest.app.databinding.FragmentSignupBinding
import com.safenest.app.ui.NestActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val signupViewModel: SignupViewModel by viewModel()
    private val binding get() = _binding!!

    private lateinit var phone : TextInputEditText
    private lateinit var signup : Button
    private lateinit var verifyOtp : Button
    private lateinit var back : ImageView
    private lateinit var otpView : OtpView
    private lateinit var loader : ProgressBar
    private lateinit var signupLayout : ConstraintLayout
    private lateinit var otpVerificationLayout : ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            phone = edtPhone
            signup = btnSignup
            verifyOtp = btnVerifyOtp
            back = btnBack
            otpView = viewOtp
            loader = progressBar
            signupLayout = layoutSignup
            otpVerificationLayout = layoutOtpVerification
        }

        var otp = ""

        signupViewModel.authStatus.observe(viewLifecycleOwner) { authState ->
            when (authState) {
                is AuthState.Success -> {
                    Toast.makeText(context, "Welcome ${authState.successMessage}", Toast.LENGTH_SHORT).show()
                    signupLayout.visibility = View.VISIBLE
                    otpVerificationLayout.visibility = View.GONE
                    redirectToNestScreen()
                }
                is AuthState.Failure -> {
                    Toast.makeText(context, "Error: ${authState.errorMessage}", Toast.LENGTH_LONG).show()
                }
                is AuthState.CodeSent -> {
                    Toast.makeText(context, "Verification code has sent via sms", Toast.LENGTH_SHORT).show()
                    loader.visibility = View.GONE
                    signupLayout.visibility = View.GONE
                    otpVerificationLayout.visibility = View.VISIBLE
                }
            }
        }

        signup.setOnClickListener {
            loader.visibility = View.VISIBLE
            signupLayout.visibility = View.GONE
            val phone = "+91"+ phone.text!!.trim().toString()
            signupViewModel.phoneAuthentication(phone)
        }

        back.setOnClickListener {
            loader.visibility = View.GONE
            otpVerificationLayout.visibility = View.GONE
            signupLayout.visibility = View.VISIBLE
        }

        verifyOtp.setOnClickListener{
            otpVerificationLayout.visibility = View.GONE
            loader.visibility = View.VISIBLE
            if(otp.length == 6){
                signupViewModel.verifyCode(otp)
            }else{
                Toast.makeText(context, "Please enter a 6 digit OTP" , Toast.LENGTH_SHORT).show()
            }
        }

        otpView.apply {
            setTextChangeListener(object : OtpView.ChangeListener {
                override fun onTextChange(value: String, completed: Boolean) {
                    otp = value
                    if(completed){
                        signupViewModel.verifyCode(value)
                    }
                }
            })
        }

    }

    private fun redirectToNestScreen(){
        val intent = Intent(activity, NestActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}