package com.moreyeahs.financeapp.presentation.auth_user.reset_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moreyeahs.financeapp.data.remote.dto.request.ResetPasswordRequest
import com.moreyeahs.financeapp.databinding.FragmentResetPasswordBinding
import com.moreyeahs.financeapp.presentation.auth_user.AuthUserActivity
import com.moreyeahs.financeapp.presentation.auth_user.login.LoginFragment
import com.moreyeahs.financeapp.util.LoaderDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentResetPasswordBinding
    private val viewModel by viewModels<ResetPasswordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater)

        val arguments = arguments
        viewModel.emailId = arguments?.getString("emailId") ?: ""

        initViews()

        return binding.root
    }

    private fun initViews() {

        binding.btnRestPassword.setOnClickListener {
            val newPassword = binding.etNewPassword.text.toString().trim()
            val reNewPassword = binding.etReNewPassword.text.toString().trim()
            if (newPassword.isNotEmpty() && reNewPassword.isNotEmpty()) {
                if (newPassword == reNewPassword) {
                    val loader = LoaderDialog(requireContext())
                    loader.showLoaderDialog()
                    viewModel.authResetPassword(
                        ResetPasswordRequest(
                            Email = viewModel.emailId,
                            password = newPassword
                        ),
                        onResponse = {
                            loader.dismissDialog()
                            (activity as AuthUserActivity).loadFragment(LoginFragment())
                        },
                        onError = {
                            loader.dismissDialog()
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(requireContext(), "Password not match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}