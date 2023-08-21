package com.moreyeahs.financeapp.presentation.auth_user.login_guest

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moreyeahs.financeapp.data.remote.dto.request.GuestLoginRequest
import com.moreyeahs.financeapp.databinding.FragmentLoginGuestBinding
import com.moreyeahs.financeapp.presentation.dashboard.MainActivity
import com.moreyeahs.financeapp.presentation.auth_user.AuthUserActivity
import com.moreyeahs.financeapp.presentation.auth_user.login.LoginFragment
import com.moreyeahs.financeapp.util.LoaderDialog
import com.moreyeahs.financeapp.util.getDeviceId
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginGuestFragment : Fragment() {

    private lateinit var binding: FragmentLoginGuestBinding
    private val viewModel by viewModels<LoginGuestViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginGuestBinding.inflate(layoutInflater)

        initViews()

        return binding.root
    }

    private fun initViews() {

        binding.btnGetStarted.setOnClickListener {
            if (isValidGuestLogin()) {
                val loader = LoaderDialog(requireContext())
                loader.showLoaderDialog()
                viewModel.guestLogin(
                    GuestLoginRequest(
                        fullName = "${binding.etFirstName.text.toString().trim()} ${binding.etLastName.text.toString().trim()}",
                        deviceId = getDeviceId(requireContext())
                    ),
                    onResponse = {
                        loader.dismissDialog()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    },
                    onError = {
                        loader.dismissDialog()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Please enter your first and last name!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivBackGuestLogin.setOnClickListener {
            (activity as AuthUserActivity).loadFragment(LoginFragment())
        }
    }

    private fun isValidGuestLogin(): Boolean {
        return binding.etFirstName.text.toString().trim().isNotEmpty() && binding.etLastName.text.toString().trim().isNotEmpty()
    }

}