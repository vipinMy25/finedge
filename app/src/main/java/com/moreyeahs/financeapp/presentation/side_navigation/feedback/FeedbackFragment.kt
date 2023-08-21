package com.moreyeahs.financeapp.presentation.side_navigation.feedback

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.moreyeahs.financeapp.data.remote.dto.request.FeedbackRequest
import com.moreyeahs.financeapp.databinding.FragmentFeedbackBinding
import com.moreyeahs.financeapp.presentation.dashboard.MainActivity
import com.moreyeahs.financeapp.util.LoaderDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackFragment : Fragment() {

    private lateinit var binding: FragmentFeedbackBinding
    private val viewModel by viewModels<FeedbackViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFeedbackBinding.inflate(layoutInflater)

        initView()

        return binding.root
    }

    private fun initView() {
        viewModel.getPreferenceData()

        binding.ivBackWebPage.setOnClickListener {
            activity?.finish()
        }

        binding.btnSubmit.setOnClickListener {
            if (binding.etDescription.text.toString().trim().isNotEmpty()) {
                val loader = LoaderDialog(requireContext())
                loader.showLoaderDialog()
                viewModel.postFeedback(
                    feedbackRequest = FeedbackRequest(
                        userId = viewModel.userId,
                        rating = binding.ratingBar.rating.toString(),
                        feedBackText = binding.etDescription.text.toString()
                    ),
                    context = requireContext(),
                    activity = activity,
                    onResponse = {
                        loader.dismissDialog()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    },
                    onError = {
                        loader.dismissDialog()
                        loader.dismissDialog()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    })
            } else {
                Toast.makeText(requireContext(), "Please enter description", Toast.LENGTH_SHORT).show()
            }
        }

    }

}