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
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.ozcanalasalvar.otp_view.view.OtpView
import com.safenest.app.databinding.FragmentSignupBinding
import com.safenest.app.util.Extension
import com.safenest.app.ui.NestActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val signupViewModel: SignupViewModel by viewModel()
    private val binding get() = _binding!!

    private lateinit var fName : TextInputEditText
    private lateinit var lName : TextInputEditText
    private lateinit var email : TextInputEditText
    private lateinit var phone : TextInputEditText
    private lateinit var password : TextInputEditText
    private lateinit var cnfPassword : TextInputEditText
    private lateinit var phoneNo : TextView
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
            fName = edtFirstName
            lName = edtLastName
            email = edtEmail
            phone = edtPhone
            password = edtPassword
            cnfPassword = edtCnfPassword
            phoneNo = txtPhoneNo
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
                    Toast.makeText(context, authState.successMessage, Toast.LENGTH_SHORT).show()
                    redirectToNestScreen()
                    signupLayout.visibility = View.VISIBLE
                    otpVerificationLayout.visibility = View.GONE
                    loader.visibility = View.GONE
                }
                is AuthState.Failure -> {
                    loader.visibility = View.GONE
                    if(authState.isSignup){
                        signupLayout.visibility = View.VISIBLE
                    }else{
                        otpVerificationLayout.visibility = View.VISIBLE
                    }
                    Toast.makeText(context, authState.errorMessage, Toast.LENGTH_LONG).show()
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
            signupViewModel.signup(
                Extension.trimString(fName.text.toString()),
                Extension.trimString(lName.text.toString()),
                Extension.trimString(email.text.toString()),
                Extension.trimString(phone.text.toString()),
                Extension.trimString(password.text.toString()),
                Extension.trimString(cnfPassword.text.toString()),
            )
            phoneNo.text = "+91" + " "+ phone.text.toString()
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
                        otpVerificationLayout.visibility = View.GONE
                        loader.visibility = View.VISIBLE
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