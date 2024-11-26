package com.safenest.app.ui.authentication.signup

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.safenest.app.databinding.FragmentSignupBinding
import com.safenest.app.ui.NestActivity

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val signupViewModel: SignupViewModel by viewModels()
    private val binding get() = _binding!!

    private lateinit var signup : Button
    private lateinit var verifyOtp : Button
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
            signup = btnSignup
            verifyOtp = btnVerifyOtp
            signupLayout = layoutSignup
            otpVerificationLayout = layoutOtpVerification
        }

        signup.setOnClickListener {
            signupLayout.visibility = View.GONE
            otpVerificationLayout.visibility = View.VISIBLE
        }

        verifyOtp.setOnClickListener{
            val intent = Intent(activity, NestActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}