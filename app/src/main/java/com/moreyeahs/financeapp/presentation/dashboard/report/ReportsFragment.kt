package com.moreyeahs.financeapp.presentation.dashboard.report

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.databinding.FragmentReportBinding
import com.moreyeahs.financeapp.domain.model.AccountModel
import com.moreyeahs.financeapp.domain.model.BankReportModel
import com.moreyeahs.financeapp.domain.model.TransactionModel
import com.moreyeahs.financeapp.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar


@AndroidEntryPoint
class ReportsFragment : Fragment() {
    private val viewModel by viewModels<ReportsViewModel>()
    private lateinit var binding: FragmentReportBinding
    private var bankReportList: ArrayList<BankReportModel> = arrayListOf()
    private var transactionModelList: List<TransactionModel> = arrayListOf()
    private lateinit var bankReportAdapter: BankReportAdapter
    private var bankListFilter: List<AccountModel> = arrayListOf()
    private val cal = Calendar.getInstance()
    private var endFilterDate: Long = 0
    private var startFilterDate: Long = 0
    private var isIncome = ObservableBoolean()
    private var isExpense = ObservableBoolean()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        endFilterDate = cal.time.time
        cal.set(Calendar.DAY_OF_MONTH, 1)
        startFilterDate = cal.time.time
        isIncome.set(true)
        viewModel.syncDatabase()
        bankListFilter = viewModel.allBankItemsFromDb
        viewModel.allTransactionListModel.observe(viewLifecycleOwner) {
            transactionModelList = it
            bankListFilter = viewModel.allBankItemsFromDb
        }
        viewModel.recentTransactionListModel.observe(viewLifecycleOwner) {
            setFilterOnTransactionList(
                list = it, isDebit = isExpense.get(), isCredit = isIncome.get()
            )
        }
        binding.tvMonthCurrent.text = Utils.getMonthYearFromLong(Calendar.getInstance().time.time)

        binding.llIncome.setOnClickListener {
            isIncome.set(true)
            isExpense.set(false)
            binding.apply {
                tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_blue))
                tvExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_grey))
                viewIncome.visibility = View.VISIBLE
                viewExpense.visibility = View.GONE
            }
            setFilterOnTransactionList(
                startFilterDate,
                endFilterDate,
                transactionModelList,
                isDebit = isExpense.get(),
                isCredit = isIncome.get()
            )
        }

        binding.llExpense.setOnClickListener {
            isIncome.set(false)
            isExpense.set(true)
            binding.apply {
                tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_grey))
                tvExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_blue))
                viewIncome.visibility = View.GONE
                viewExpense.visibility = View.VISIBLE
            }
            setFilterOnTransactionList(
                startFilterDate,
                endFilterDate,
                transactionModelList,
                isDebit = isExpense.get(),
                isCredit = isIncome.get()
            )
        }

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
            setFilterOnTransactionList(
                startFilterDate,
                endFilterDate,
                transactionModelList,
                isDebit = isExpense.get(),
                isCredit = isIncome.get()
            )
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

                calendar.set(
                    Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
                endFilterDate = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                startFilterDate = calendar.timeInMillis
                setFilterOnTransactionList(
                    startFilterDate,
                    endFilterDate,
                    transactionModelList,
                    isDebit = isExpense.get(),
                    isCredit = isIncome.get()
                )
            }
        }



        viewModel.creditAmount.observe(viewLifecycleOwner) {
            println("income** ${"₹ " + String.format("%.2f", it)}")
            // binding.tvIncome.text = "₹ " + String.format("%.2f", it)
        }

        viewModel.debitAmount.observe(viewLifecycleOwner) {
            println("debit** ${"₹ " + String.format("%.2f", it)}")
            //binding.tvExpenseAmount.text = "₹ " + String.format("%.2f", it)
        }
    }

    private fun initChart(finalBankReportList: List<BankReportModel>) {
        val pieEntries = ArrayList<PieEntry>()
        binding.pieChart.description.isEnabled = false
        binding.pieChart.extraBottomOffset = 20f
        binding.pieChart.extraLeftOffset = 30f
        binding.pieChart.extraRightOffset = 30f
        binding.pieChart.dragDecelerationFrictionCoef = 0.95f
        binding.pieChart.isDrawHoleEnabled = true
        binding.pieChart.setHoleColor(Color.WHITE)
        binding.pieChart.setTransparentCircleColor(Color.WHITE)
        binding.pieChart.setTransparentCircleAlpha(110)
        binding.pieChart.holeRadius = 58f
        binding.pieChart.transparentCircleRadius = 10f
        binding.pieChart.setDrawCenterText(true)
        binding.pieChart.rotationAngle = 0f

        binding.pieChart.isRotationEnabled = true
        binding.pieChart.isHighlightPerTapEnabled = true
        binding.pieChart.animateY(1400, Easing.EaseInOutQuad)
        binding.pieChart.setEntryLabelColor(ContextCompat.getColor(requireContext(), R.color.black))

        binding.pieChart.setEntryLabelTextSize(12f)
        binding.pieChart.setHoleColor(
            ContextCompat.getColor(
                requireContext(), R.color.background_grey
            )
        )
        binding.pieChart.legend.isEnabled = false
        val typeAmountMap: MutableMap<String, Double?> = HashMap()
        finalBankReportList.forEach { bankItem ->
            typeAmountMap[bankItem.bankName.ifEmpty { "UPI" }] = bankItem.amount
        }
        val colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(requireContext(), R.color.pie_color_one))
        colors.add(ContextCompat.getColor(requireContext(), R.color.pie_color_two))
        colors.add(ContextCompat.getColor(requireContext(), R.color.pie_color_three))
        colors.add(ContextCompat.getColor(requireContext(), R.color.pie_color_four))

        for (type in typeAmountMap.keys) {
            typeAmountMap[type]?.let { PieEntry(it.toFloat(), type) }?.let { pieEntries.add(it) }
        }
        val dataSet = PieDataSet(pieEntries, "Bank")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.valueTextSize = 12f
        dataSet.colors = colors
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        val pieData = PieData(dataSet)
        pieData.setDrawValues(true)
        pieData.setValueTextSize(10f)


        dataSet.valueFormatter = PercentFormatter()
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.data = pieData
        binding.pieChart.invalidate()
    }

    private fun setFilterOnTransactionList(
        startFilterDate: Long? = null,
        endFilterDate: Long? = null,
        list: List<TransactionModel>,
        isDebit: Boolean,
        isCredit: Boolean
    ) {
        bankReportList.clear()
        list.let { allSmsList ->
            allSmsList.forEach {
                bankReportList.add(
                    BankReportModel(
                        bankName = it.bankName,
                        accountNo = it.accountNumber,
                        amount = if (it.amount.isEmpty()) 0.0 else it.amount.toDouble(),
                        transactionType = it.transactionType,
                        smsTime = it.timestamp,
                        bankLogoUrl = it.bankLogoUrl
                    )
                )
            }
            if (startFilterDate != null) {
                bankReportList = bankReportList.filter {
                    it.smsTime.toLong() in startFilterDate..endFilterDate!!
                } as ArrayList<BankReportModel>
            }
            if (isDebit) {
                bankReportList = bankReportList.filter {
                    it.transactionType.equals("Debit", true)
                } as ArrayList<BankReportModel>
            }

            if (isCredit) {
                bankReportList = bankReportList.filter {
                    it.transactionType.equals("Credit", true)
                } as ArrayList<BankReportModel>
            }

            val bankReportModelMap: MutableMap<String, BankReportModel> = HashMap()
            for (reportItem in bankReportList) {
                val current: BankReportModel? = bankReportModelMap[reportItem.accountNo]
                if (current == null) {
                    bankReportModelMap[reportItem.accountNo] = reportItem
                } else {
                    current.amount = current.amount?.plus(reportItem.amount ?: 0.0)
                }
            }
            println("bankReportMap*** $bankReportModelMap")
            var finalBankReportList: List<BankReportModel> = bankReportModelMap.values.toList().sortedByDescending { it.amount }
            bankReportAdapter =
                BankReportAdapter(requireContext(), finalBankReportList)
            println("finalBankReportList*** $finalBankReportList")
            binding.rvAccounts.apply {
                adapter = bankReportAdapter
                hasFixedSize()

            }
            initChart(finalBankReportList)
            viewModel.getIncomeExpenseByReport(finalBankReportList)
        }
    }
}