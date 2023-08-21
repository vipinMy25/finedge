package com.moreyeahs.financeapp.presentation.auth_user.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moreyeahs.financeapp.data.remote.dto.request.AuthLoginRequest
import com.moreyeahs.financeapp.databinding.FragmentLoginBinding
import com.moreyeahs.financeapp.presentation.dashboard.MainActivity
import com.moreyeahs.financeapp.presentation.auth_user.AuthUserActivity
import com.moreyeahs.financeapp.presentation.auth_user.forgot_password.ForgotPasswordFragment
import com.moreyeahs.financeapp.presentation.auth_user.login_guest.LoginGuestFragment
import com.moreyeahs.financeapp.presentation.auth_user.otp_verification.EnterOtpFragment
import com.moreyeahs.financeapp.presentation.auth_user.register.SignUpFragment
import com.moreyeahs.financeapp.util.LoaderDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.tvLoginAsGuest.setOnClickListener {
            (activity as AuthUserActivity).loadFragment(LoginGuestFragment())
        }

        binding.tvSignUp.setOnClickListener {
            (activity as AuthUserActivity).loadFragment(SignUpFragment())
        }

        binding.tvForgotPassword.setOnClickListener {
            (activity as AuthUserActivity).loadFragment(ForgotPasswordFragment())
        }

        binding.btnSingIn.setOnClickListener {
            if (binding.etEmailLogin.text.toString().trim().isNotEmpty() && binding.etPasswordLogin.text.toString().trim().isNotEmpty()) {
                val loader = LoaderDialog(requireContext())
                loader.showLoaderDialog()
                viewModel.authUserLogin(
                    AuthLoginRequest(
                        Email = binding.etEmailLogin.text.toString().trim(),
                        password = binding.etPasswordLogin.text.toString().trim()
                    ),
                    binding.cbRememberMe.isChecked,
                    onResponse = {
                        loader.dismissDialog()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    },
                    onError = {
                        loader.dismissDialog()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        if (it.equals("To login please verify the otp sent to your mail", true)) {
                            val bundle = Bundle()
                            bundle.putString("emailId", binding.etEmailLogin.text.toString().trim())
                            bundle.putString("fromFrag", "Login")
                            val enterOtpFragment = EnterOtpFragment()
                            enterOtpFragment.arguments = bundle
                            (activity as AuthUserActivity).loadFragment(enterOtpFragment)
                        }
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}