package com.moreyeahs.financeapp.presentation.auth_user.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moreyeahs.financeapp.data.remote.dto.request.AuthRegisterRequest
import com.moreyeahs.financeapp.databinding.FragmentSignUpBinding
import com.moreyeahs.financeapp.presentation.auth_user.AuthUserActivity
import com.moreyeahs.financeapp.presentation.auth_user.login.LoginFragment
import com.moreyeahs.financeapp.presentation.auth_user.otp_verification.EnterOtpFragment
import com.moreyeahs.financeapp.presentation.side_navigation.web_page.WebPageFragment
import com.moreyeahs.financeapp.util.Constants
import com.moreyeahs.financeapp.util.LoaderDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)

        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.tvSingIn.setOnClickListener {
            (activity as AuthUserActivity).loadFragment(LoginFragment())
        }

        binding.tvTermsCondition.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("WebUrl", Constants.TERMS_CONDITION_URL)
            bundle.putString("Title", "Terms & Conditions")
            bundle.putString("From", "SignUp")
            val fragment = WebPageFragment()
            fragment.arguments = bundle
            (activity as AuthUserActivity).loadFragment(fragment)
        }

        binding.btnSingUp.setOnClickListener {
            if (binding.etNameSignup.text.toString().trim().isNotEmpty() &&
                binding.etMobileSignup.text.toString().trim().isNotEmpty() &&
                binding.etEmailSingup.toString().trim().isNotEmpty() &&
                binding.etPasswordSignup.text.toString().trim().isNotEmpty()
            ) {
                if (binding.cbTermsCondition.isChecked) {
                    val loader = LoaderDialog(requireContext())
                    loader.showLoaderDialog()
                    viewModel.authUserRegister(
                        AuthRegisterRequest(
                            fullName = binding.etNameSignup.text.toString().trim(),
                            Email = binding.etEmailSingup.text.toString().trim(),
                            password = binding.etPasswordSignup.text.toString().trim(),
                            confirm_password = binding.etPasswordSignup.text.toString().trim(),
                            Mobile_No = binding.etMobileSignup.text.toString().trim()
                        ), onResponse = {
                            loader.dismissDialog()
                            val bundle = Bundle()
                            bundle.putString("emailId", binding.etEmailSingup.text.toString().trim())
                            bundle.putString("fromFrag", "SignUp")
                            val enterOtpFragment = EnterOtpFragment()
                            enterOtpFragment.arguments = bundle
                            (activity as AuthUserActivity).loadFragment(enterOtpFragment)
                        },
                        onError = {
                            loader.dismissDialog()
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(requireContext(), "Please accept Terms & Conditions!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}