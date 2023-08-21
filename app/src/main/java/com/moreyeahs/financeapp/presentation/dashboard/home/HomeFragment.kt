package com.moreyeahs.financeapp.presentation.dashboard.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.databinding.FragmentHomeBinding
import com.moreyeahs.financeapp.domain.model.BankModel
import com.moreyeahs.financeapp.domain.model.BankFilterModel
import com.moreyeahs.financeapp.presentation.dashboard.MainActivity
import com.moreyeahs.financeapp.util.Utils
import com.moreyeahs.financeapp.util.toPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var homeAdaptor: HomeTransactionAdapter

    var activity: MainActivity? = null

    private var bankListFilter: List<BankFilterModel> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        activity = (getActivity() as MainActivity?)

        homeAdaptor = HomeTransactionAdapter(requireContext())

        initViews()

        return binding.root
    }

    private fun initViews() {

        viewModel.syncDatabase()

        binding.rvRecentTransactions.apply {
            adapter = homeAdaptor
        }

        endFilterDate = cal.time.time
        cal.set(Calendar.DAY_OF_MONTH, 1)
        startFilterDate = cal.time.time

        viewModel.recentTransactionListModel.observe(viewLifecycleOwner, Observer {
            homeAdaptor.transactionModelItems = it
            homeAdaptor.notifyDataSetChanged()
            binding.rvRecentTransactions.post { binding.rvRecentTransactions.scrollToPosition(0) }
        })

        viewModel.allBankList.observe(viewLifecycleOwner) {
            bankListFilter = it
        }

        viewModel.creditAmount.observe(viewLifecycleOwner) {
            binding.tvIncome.text = "₹ " + String.format("%.2f", it)
        }

        viewModel.debitAmount.observe(viewLifecycleOwner) {
            binding.tvExpenseAmount.text = "₹ " + String.format("%.2f", it)
        }

        viewModel.balanceAmount.observe(viewLifecycleOwner) {
            binding.tvBalance.text = "₹ " + String.format("%.2f", it)
            if (it > 0) {
                binding.tvBalance.setTextColor(requireContext().resources.getColor(R.color.green, null))
            } else {
                binding.tvBalance.setTextColor(requireContext().resources.getColor(R.color.red, null))
            }
        }

        viewModel.dailyAvgExpense.observe(viewLifecycleOwner) {
            binding.tvAvgDailyExpense.text = "₹ " + String.format("%.2f", it)
        }

        binding.tvMonthCurrent.text = Utils.getMonthYearFromLong(Calendar.getInstance().time.time)

        binding.ivMonthBefore.setOnClickListener {
            val curMonthVisible = binding.tvMonthCurrent.text.toString().trim()
            val timeFromMonthYearString = Utils.getTimeFromMonthYearString(curMonthVisible)
            val calen = Calendar.getInstance()
            calen.timeInMillis = timeFromMonthYearString
            calen.add(Calendar.MONTH, -1)
            binding.tvMonthCurrent.text = Utils.getMonthYearFromLong(calen.timeInMillis)

            calen.set(Calendar.DAY_OF_MONTH, calen.getActualMaximum(Calendar.DAY_OF_MONTH))
            endFilterDate = calen.timeInMillis
            calen.set(Calendar.DAY_OF_MONTH, 1)
            startFilterDate = calen.timeInMillis
            lifecycleScope.launch {
                viewModel.allTransactionListModel.value?.let { allSmsList ->
                    val filteredByBank = allSmsList.filter { sms ->
                        sms.timestamp.toLong() in startFilterDate..endFilterDate
                    }
                    viewModel.getAllBank(filteredByBank)
                }
            }
            setFilterOnTransactionList(startFilterDate, endFilterDate, isDebit = false, isCredit = false)
        }

        binding.ivMonthAfter.setOnClickListener {
            val curMonthVisible = binding.tvMonthCurrent.text.toString().trim()
            val timeFromMonthYearString = Utils.getTimeFromMonthYearString(curMonthVisible)
            val calen = Calendar.getInstance()
            calen.add(Calendar.MONTH, -1)
            val curDateTime = calen.timeInMillis

            if (curDateTime > timeFromMonthYearString) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = timeFromMonthYearString
                calendar.add(Calendar.MONTH, 1)
                binding.tvMonthCurrent.text = Utils.getMonthYearFromLong(calendar.timeInMillis)

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                endFilterDate = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                startFilterDate = calendar.timeInMillis
                lifecycleScope.launch {
                    viewModel.allTransactionListModel.value?.let { allSmsList ->
                        val filteredByBank = allSmsList.filter { sms ->
                            sms.timestamp.toLong() in startFilterDate..endFilterDate
                        }
                        viewModel.getAllBank(filteredByBank)
                    }
                }
                setFilterOnTransactionList(startFilterDate, endFilterDate, isDebit = false, isCredit = false)
            }
        }

        binding.ivFilterTransaction.setOnClickListener {
            showFilterDialog()
        }

        homeAdaptor.editClick = {
            AddExpenseBottomSheet(requireActivity(), it, viewModel.allBankItemsFromDb) { sms ->
                lifecycleScope.launch {
                    val updateSms = viewModel.updateSms(sms, requireContext(), activity)
                    updateSms.join()
                    val allSmsFromDb = viewModel.getAllSmsFromDb()
                    allSmsFromDb.join()
                    Toast.makeText(requireContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show()
                }
            }.show(childFragmentManager, "")
        }

        homeAdaptor.deleteClick = {
            lifecycleScope.launch {
                val deleteSmsLocally = viewModel.deleteSmsLocally(it, requireContext(), activity)
                deleteSmsLocally.join()
                val allSmsFromDb = viewModel.getAllSmsFromDb()
                allSmsFromDb.join()
                Toast.makeText(requireContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show()
            }
        }

        activity?.binding?.toolbar?.ivMenuAction?.setOnClickListener {

            AddExpenseBottomSheet(requireActivity(), null, viewModel.allBankItemsFromDb) { sms ->
                lifecycleScope.launch {
                    val insertSmsJob = viewModel.addTransaction(sms, requireContext(), activity)
                    insertSmsJob.join()
                    val allSmsFromDb = viewModel.getAllSmsFromDb()
                    allSmsFromDb.join()
                    Toast.makeText(requireContext(), "Expense Added Successfully!", Toast.LENGTH_SHORT).show()
                }
            }.show(childFragmentManager, "")
        }
    }

    private val cal = Calendar.getInstance()
    private var endFilterDate: Long = 0
    private var startFilterDate: Long = 0
    private var startFilterDateSelected: Long = 0
    private var endFilterDateSelected: Long = 0
    private var isDebitSelected = false
    private var isCreditSelected = false
    private fun showFilterDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_filter_transaction)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val rvBank = dialog.findViewById(R.id.rv_bank_filter) as RecyclerView

        val bankAdapter = SelectBankFilterAdapter(requireContext())
        bankAdapter.banksList = bankListFilter

        rvBank.apply {
            adapter = bankAdapter
        }

        val tvDateFilter = dialog.findViewById(R.id.tv_date_filter) as TextView
        val tvDebit = dialog.findViewById(R.id.tv_debit_filter) as TextView
        val tvCredit = dialog.findViewById(R.id.tv_credit_filter) as TextView

        val btnApply = dialog.findViewById(R.id.btn_apply_filter) as Button
        val btnCancel = dialog.findViewById(R.id.btn_cancel_filter) as Button
        val tvClearFilter = dialog.findViewById(R.id.tv_clear_all_filter) as TextView

        if (isDebitSelected) {
            tvDebit.background = resources.getDrawable(R.drawable.rounded_sides_blue_background, null)
            tvDebit.setTextColor(resources.getColor(R.color.white, null))
            tvCredit.background = resources.getDrawable(R.drawable.rounded_sides_outline_background, null)
            tvCredit.setTextColor(resources.getColor(R.color.text_grey, null))
            tvDebit.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
            tvCredit.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
        }

        if (isCreditSelected) {
            tvCredit.background = resources.getDrawable(R.drawable.rounded_sides_blue_background, null)
            tvCredit.setTextColor(resources.getColor(R.color.white, null))
            tvDebit.background = resources.getDrawable(R.drawable.rounded_sides_outline_background, null)
            tvDebit.setTextColor(resources.getColor(R.color.text_grey, null))
            tvDebit.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
            tvCredit.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
        }

        if (startFilterDateSelected == 0L || endFilterDateSelected == 0L) {
            tvDateFilter.text = "${Utils.getDateFromLong(startFilterDate)} - ${Utils.getDateFromLong(endFilterDate)}"
        } else {
            tvDateFilter.text = "${Utils.getDateFromLong(startFilterDateSelected)} - ${Utils.getDateFromLong(endFilterDateSelected)}"
        }
        var startDate: Long = 0
        var endDate: Long = 0
        tvDateFilter.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker().setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar).build()
            datePicker.show(childFragmentManager, "DatePicker")

            // Setting up the event for when ok is clicked
            datePicker.addOnPositiveButtonClickListener {
                startDate = it.first
                endDate = it.second
                tvDateFilter.text = datePicker.headerText
                viewModel.allTransactionListModel.value?.let { allSmsList ->
                    lifecycleScope.launch {
                        val filteredByBank = allSmsList.filter { sms ->
                            sms.timestamp.toLong() in startDate..endDate
                        }
                        val allBankJob = viewModel.getAllBank(filteredByBank)
                        allBankJob.join()
                        bankAdapter.banksList = bankListFilter
                        bankAdapter.notifyDataSetChanged()
                    }
                }
            }

            // Setting up the event for when cancelled is clicked
            datePicker.addOnNegativeButtonClickListener {
                //
            }

            // Setting up the event for when back button is pressed
            datePicker.addOnCancelListener {
                //
            }
        }

        var isDebit = false
        var isCredit = false
        tvDebit.setOnClickListener {
            isDebit = !isDebit
            isCredit = false
            if (isDebit) {
                tvDebit.background = resources.getDrawable(R.drawable.rounded_sides_blue_background, null)
                tvDebit.setTextColor(resources.getColor(R.color.white, null))
                tvCredit.background = resources.getDrawable(R.drawable.rounded_sides_outline_background, null)
                tvCredit.setTextColor(resources.getColor(R.color.text_grey, null))
            } else {
                tvDebit.background = resources.getDrawable(R.drawable.rounded_sides_outline_background, null)
                tvDebit.setTextColor(resources.getColor(R.color.text_blue, null))
                tvCredit.setTextColor(resources.getColor(R.color.text_blue, null))
            }
            tvDebit.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
            tvCredit.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
        }
        tvCredit.setOnClickListener {
            isCredit = !isCredit
            isDebit = false
            if (isCredit) {
                tvCredit.background = resources.getDrawable(R.drawable.rounded_sides_blue_background, null)
                tvCredit.setTextColor(resources.getColor(R.color.white, null))
                tvDebit.background = resources.getDrawable(R.drawable.rounded_sides_outline_background, null)
                tvDebit.setTextColor(resources.getColor(R.color.text_grey, null))
            } else {
                tvCredit.background = resources.getDrawable(R.drawable.rounded_sides_outline_background, null)
                tvCredit.setTextColor(resources.getColor(R.color.text_blue, null))
                tvDebit.setTextColor(resources.getColor(R.color.text_blue, null))
            }
            tvDebit.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
            tvCredit.setPadding(10.toPx, 5.toPx, 10.toPx, 5.toPx)
        }

        btnApply.setOnClickListener {
            if (startDate > 0 && endDate > 0) {
                startFilterDateSelected = startDate
                endFilterDateSelected = endDate
                setFilterOnTransactionList(startDate, endDate, isDebit, isCredit)
            } else {
                if (startFilterDateSelected == 0L || endFilterDateSelected == 0L) {
                    startFilterDateSelected = startFilterDate
                    endFilterDateSelected = endFilterDate
                    setFilterOnTransactionList(startFilterDate, endFilterDate, isDebit, isCredit)
                } else {
                    setFilterOnTransactionList(startFilterDateSelected, endFilterDateSelected, isDebit, isCredit)
                }
            }
            isDebitSelected = isDebit
            isCreditSelected = isCredit
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            if (startFilterDateSelected == 0L || endFilterDateSelected == 0L) {
                lifecycleScope.launch {
                    val allBankJob = viewModel.getAllBank(viewModel.recentTransactionListModel.value ?: listOf())
                    allBankJob.join()
                    bankAdapter.banksList = bankListFilter
                    bankAdapter.notifyDataSetChanged()
                }
            }
            dialog.dismiss()
        }

        tvClearFilter.setOnClickListener {
            homeAdaptor.transactionModelItems = viewModel.recentTransactionListModel.value ?: listOf()
            viewModel.getIncomeExpense(viewModel.recentTransactionListModel.value ?: listOf())
            viewModel.getDailyAvgExpense(viewModel.recentTransactionListModel.value ?: listOf(), startFilterDate, endFilterDate)
            bankListFilter.forEach { bankItem ->
                bankItem.isSelected = false
            }
            isCreditSelected = false
            isDebitSelected = false
            startFilterDateSelected = 0
            endFilterDateSelected = 0
            lifecycleScope.launch {
                val allBankJob = viewModel.getAllBank(viewModel.recentTransactionListModel.value ?: listOf())
                allBankJob.join()
                bankAdapter.banksList = bankListFilter
                bankAdapter.notifyDataSetChanged()
            }
            binding.tvMonthCurrent.text = Utils.getMonthYearFromLong(Calendar.getInstance().time.time)
            endFilterDate = cal.time.time
            cal.set(Calendar.DAY_OF_MONTH, 1)
            startFilterDate = cal.time.time
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setFilterOnTransactionList(startFilterDate: Long, endFilterDate: Long, isDebit: Boolean, isCredit: Boolean) {
        viewModel.allTransactionListModel.value?.let { allSmsList ->
            var filteredByBank = allSmsList.filter { sms ->
                val filterBankList = bankListFilter.filter { bankItemFilter ->
                    bankItemFilter.isSelected
                }
                val bankName = sms.bankName
                val accountNo = sms.accountNumber
                if (filterBankList.isEmpty()) {
                    true
                } else {
                    val bankModels = filterBankList.map { itemFilter ->
                        BankModel(itemFilter.bankName, itemFilter.accountNo)
                    }
                    bankModels.contains(BankModel(bankName, accountNo))
                }
            }
            filteredByBank = filteredByBank.filter {
                it.timestamp.toLong() in startFilterDate..endFilterDate
            }
            if (isDebit) {
                filteredByBank = filteredByBank.filter {
                    it.transactionType.equals("Debit", true)
                }
            }
            if (isCredit) {
                filteredByBank = filteredByBank.filter {
                    it.transactionType.equals("Credit", true)
                }
            }

            homeAdaptor.transactionModelItems = filteredByBank
            viewModel.getIncomeExpense(filteredByBank)
            viewModel.getDailyAvgExpense(filteredByBank, startFilterDate, endFilterDate)
        }
    }

}