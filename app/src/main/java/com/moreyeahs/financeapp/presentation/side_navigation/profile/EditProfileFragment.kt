package com.moreyeahs.financeapp.presentation.side_navigation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moreyeahs.financeapp.data.remote.dto.request.EditProfileGuestRequest
import com.moreyeahs.financeapp.data.remote.dto.request.EditProfileUserRequest
import com.moreyeahs.financeapp.databinding.FragmentEditProfileBinding
import com.moreyeahs.financeapp.presentation.auth_user.otp_verification.EnterOtpFragment
import com.moreyeahs.financeapp.presentation.side_navigation.SideNavigationActivity
import com.moreyeahs.financeapp.util.LoaderDialog
import com.moreyeahs.financeapp.util.getDeviceId
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel by viewModels<EditProfileViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)

        initViews()

        return binding.root
    }

    private fun initViews() {

        viewModel.getPreferenceData()

        binding.etNameEditProfile.setText(viewModel.userName)
        binding.etMobileEditProfile.setText(viewModel.userMobile)
        binding.etEmailEditProfile.setText(viewModel.userEmail)

        binding.btnCancelEditProfile.setOnClickListener {
            activity?.finish()
        }

        binding.btnSaveEditProfile.setOnClickListener {

            val name = binding.etNameEditProfile.text.toString().trim()
            val email = binding.etEmailEditProfile.text.toString().trim()
            val mobile = binding.etMobileEditProfile.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Name can't be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (viewModel.userEmail.isNotEmpty() && email.isEmpty()) {
                Toast.makeText(requireContext(), "Email can't be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loader = LoaderDialog(requireContext())
            loader.showLoaderDialog()

            if (viewModel.isGuestUser) {
                viewModel.updateGuestProfile(
                    EditProfileGuestRequest(
                        fullName = name,
                        deviceId = getDeviceId(requireContext()),
                        Email = email,
                        mobileNumber = mobile
                    ),
                    context = requireContext(),
                    activity = activity,
                    onResponse = {
                        loader.dismissDialog()
                        if (viewModel.userName != name) {
                            viewModel.updateNameInPrefs(name)
                        }
                        if (viewModel.userMobile != mobile) {
                            viewModel.updateMobileInPrefs(mobile)
                        }
                        if (email.isEmpty()) {
                            Toast.makeText(requireContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                            activity?.finish()
                        } else if (viewModel.userEmail.equals(email, true)) {
                            Toast.makeText(requireContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                            activity?.finish()
                        } else {
                            val bundle = Bundle()
                            bundle.putString("emailId", email)
                            bundle.putString("fromFrag", "EditProfile")
                            val enterOtpFragment = EnterOtpFragment()
                            enterOtpFragment.arguments = bundle
                            (activity as SideNavigationActivity).loadFragment(enterOtpFragment)
                        }
                    },
                    onError = {
                        loader.dismissDialog()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                viewModel.updateUserProfile(
                    EditProfileUserRequest(
                        fullName = name,
                        Email = email,
                        Mobile_No = mobile
                    ),
                    context = requireContext(),
                    activity = activity,
                    onResponse = {
                        loader.dismissDialog()
                        if (viewModel.userName != name) {
                            viewModel.updateNameInPrefs(name)
                        }
                        if (viewModel.userMobile != mobile) {
                            viewModel.updateMobileInPrefs(mobile)
                        }
                        if (viewModel.userEmail.equals(email, true)) {
                            Toast.makeText(requireContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                            activity?.finish()
                        } else {
                            val bundle = Bundle()
                            bundle.putString("emailId", email)
                            bundle.putString("fromFrag", "EditProfile")
                            val enterOtpFragment = EnterOtpFragment()
                            enterOtpFragment.arguments = bundle
                            (activity as SideNavigationActivity).loadFragment(enterOtpFragment)
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