package com.moreyeahs.financeapp.presentation.dashboard.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.moreyeahs.financeapp.databinding.FragmentAllAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllAccountFragment : Fragment() {

    private lateinit var binding: FragmentAllAccountBinding
    private val viewModel by viewModels<AllAccountViewModel>()
    private lateinit var allAccountAdapter: AllAccountAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAllAccountBinding.inflate(layoutInflater)

        initViews()

        return binding.root
    }

    private fun initViews() {
        lifecycleScope.launch {
            val allAccountsFromDb = viewModel.getAllAccountsFromDb()
            allAccountsFromDb.join()

            val categorisedList = viewModel.getCategorisedList()

            allAccountAdapter = AllAccountAdapter(requireContext(), activity, categorisedList, viewModel.financeRepo, viewModel.preferencesManager) {
                notifyAdapter()
            }

            binding.rvAccounts.adapter = allAccountAdapter
        }
    }

    private fun notifyAdapter() {
        lifecycleScope.launch {
            val allAccountsFromDb = viewModel.getAllAccountsFromDb()
            allAccountsFromDb.join()
            allAccountAdapter.notifyList(viewModel.getCategorisedList())
        }
    }

}