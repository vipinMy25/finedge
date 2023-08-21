package com.moreyeahs.financeapp.presentation.auth_user.otp_verification

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moreyeahs.financeapp.data.remote.dto.request.VerifyOtpRequest
import com.moreyeahs.financeapp.databinding.FragmentEnterOtpBinding
import com.moreyeahs.financeapp.presentation.dashboard.MainActivity
import com.moreyeahs.financeapp.presentation.auth_user.AuthUserActivity
import com.moreyeahs.financeapp.presentation.auth_user.register.SignUpFragment
import com.moreyeahs.financeapp.presentation.auth_user.reset_password.ResetPasswordFragment
import com.moreyeahs.financeapp.util.LoaderDialog
import com.moreyeahs.financeapp.util.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EnterOtpFragment : Fragment() {

    private lateinit var binding: FragmentEnterOtpBinding
    private val viewModel by viewModels<EnterOtpViewModel>()

    @Inject
    lateinit var prefs: PreferencesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEnterOtpBinding.inflate(layoutInflater)

        val arguments = arguments
        viewModel.emailId = arguments?.getString("emailId") ?: ""
        viewModel.fromFrag = arguments?.getString("fromFrag") ?: ""

        initViews()

        return binding.root
    }

    private fun initViews() {

        binding.tvEmailOtp.text = viewModel.emailId

        binding.ivBackEnterOtp.setOnClickListener {
            (activity as AuthUserActivity).loadFragment(SignUpFragment())
        }

        binding.btnVerifyOtp.setOnClickListener {
            val otp = binding.pinview.text.toString().trim()
            if (otp.isNotEmpty()) {
                val loader = LoaderDialog(requireContext())
                loader.showLoaderDialog()
                viewModel.verifyOtp(
                    VerifyOtpRequest(
                        Email = viewModel.emailId,
                        otp = otp
                    ),
                    onResponse = {
                        loader.dismissDialog()
                        when (viewModel.fromFrag) {
                            "SignUp" -> {
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            }

                            "ForgotPassword" -> {
                                val bundle = Bundle()
                                bundle.putString("emailId", viewModel.emailId)
                                val resetPasswordFragment = ResetPasswordFragment()
                                resetPasswordFragment.arguments = bundle
                                (activity as AuthUserActivity).loadFragment(resetPasswordFragment)
                            }

                            "EditProfile" -> {
                                Toast.makeText(requireContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                                activity?.finish()
                            }

                            "Login" -> {
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            }
                        }
                    },
                    onError = {
                        loader.dismissDialog()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

}