package com.moreyeahs.financeapp.presentation.auth_user.forgot_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moreyeahs.financeapp.data.remote.dto.request.ForgetPasswordRequest
import com.moreyeahs.financeapp.databinding.FragmentForgotPasswordBinding
import com.moreyeahs.financeapp.presentation.auth_user.AuthUserActivity
import com.moreyeahs.financeapp.presentation.auth_user.otp_verification.EnterOtpFragment
import com.moreyeahs.financeapp.util.LoaderDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel by viewModels<ForgotPasswordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)

        initViews()

        return binding.root
    }

    private fun initViews() {

        binding.btnContinue.setOnClickListener {
            if (binding.etEmailForgetPassword.text.toString().trim().isNotEmpty()) {
                val loader = LoaderDialog(requireContext())
                loader.showLoaderDialog()
                viewModel.authForgetPassword(
                    ForgetPasswordRequest(
                        Email = binding.etEmailForgetPassword.text.toString().trim(),
                    ),
                    onResponse = {
                        loader.dismissDialog()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        val bundle = Bundle()
                        bundle.putString("emailId", binding.etEmailForgetPassword.text.toString().trim())
                        bundle.putString("fromFrag", "ForgotPassword")
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
                Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}