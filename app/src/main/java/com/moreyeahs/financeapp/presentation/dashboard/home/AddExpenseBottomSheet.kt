package com.moreyeahs.financeapp.presentation.dashboard.home

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.moreyeahs.financeapp.R
import com.moreyeahs.financeapp.databinding.BottomSheetAddExpenseBinding
import com.moreyeahs.financeapp.domain.model.AccountModel
import com.moreyeahs.financeapp.domain.model.TransactionModel
import com.moreyeahs.financeapp.util.Utils
import java.util.Calendar
import java.util.Locale

class AddExpenseBottomSheet(
    val context: Activity,
    var item: TransactionModel?,
    var allBankItemsFromDb: List<AccountModel>,
    var okClick: ((TransactionModel) -> Unit)? = null
) : BottomSheetDialogFragment(), RecognitionListener {

    private lateinit var binding: BottomSheetAddExpenseBinding
    private lateinit var speechRecognizer: SpeechRecognizer
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    private var selectedTimeInMillis: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_add_expense, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer.setRecognitionListener(this)

        val accountSpinnerAdapter = AccountSpinnerAdapter(requireContext(), allBankItemsFromDb)
        binding.acsAccounts.adapter = accountSpinnerAdapter

        if (item != null) {
            binding.apply {
                etAmount.setText(item?.amount)
                etTitle.setText(item?.sms)
                etDate.setText(Utils.getDateTime(item?.timestamp!!.toLong()))
                if (item?.transactionType.equals("Credit", true)) {
                    scExpense.isChecked = false
                }
                tvAddExpenseTitle.text = "Edit Expense"
                etExpenseType.setText(item?.accountType)

                if (allBankItemsFromDb.isNotEmpty()) {
                    val indexOfFirst = allBankItemsFromDb.indexOfFirst { it.accountNumber.equals(item?.accountNumber, true) && it.bankName.equals(item?.bankName, true) }
                    binding.acsAccounts.setSelection(indexOfFirst, true)
                } else {
                    binding.acsAccounts.visibility = View.GONE
                }
            }
        } else {
            if (allBankItemsFromDb.isNotEmpty()) {
                val indexOfFirst = allBankItemsFromDb.indexOfFirst { it.isDefault }
                binding.acsAccounts.setSelection(indexOfFirst, true)
            } else {
                binding.acsAccounts.visibility = View.GONE
            }
        }

        binding.ivAddExpenseMic.setOnClickListener {
            startSpeechRecognition()
        }

        binding.lavAddExpense.setOnClickListener {
            speechRecognizer.cancel()
            binding.lavAddExpense.cancelAnimation()
            binding.lavAddExpense.visibility = View.GONE
            binding.ivAddExpenseMic.visibility = View.VISIBLE
        }

        binding.etExpenseType.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it, R.style.PopupMenu)

            popupMenu.setForceShowIcon(true)
            popupMenu.menuInflater.inflate(R.menu.expense_type_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.bank_acc_type_menu -> {
                        binding.etExpenseType.setText(menuItem.title)
                    }

                    R.id.credit_card_type_menu -> {
                        binding.etExpenseType.setText(menuItem.title)
                    }

                    R.id.loan_type_menu -> {
                        binding.etExpenseType.setText(menuItem.title)
                    }

                    R.id.cash_type_menu -> {
                        binding.etExpenseType.setText(menuItem.title)
                    }
                }
                true
            }
            popupMenu.show()
        }

        binding.etDate.setOnClickListener {
            showDatePicker(binding.etDate)
        }

        binding.btnCancel.setOnClickListener { dialog?.dismiss() }

        binding.btnSaveExpense.setOnClickListener {
            okClick?.let { vm ->
                if (item != null) {
                    val sms = item?.copy(
                        timestamp = if (selectedTimeInMillis == 0L) item?.timestamp ?: "" else selectedTimeInMillis.toString(),
                        amount = binding.etAmount.text.toString(),
                        sms = binding.etTitle.text.toString(),
                        transactionType = if (binding.scExpense.isChecked) "Debit" else "Credit",
                        accountType = binding.etExpenseType.text.toString().trim(),
                        bankName = if (allBankItemsFromDb.isNotEmpty()) allBankItemsFromDb[binding.acsAccounts.selectedItemPosition].bankName else item?.bankName ?: "Self",
                        accountNumber = if (allBankItemsFromDb.isNotEmpty()) allBankItemsFromDb[binding.acsAccounts.selectedItemPosition].accountNumber else item?.accountNumber ?: ""
                    )
                    sms?.let {
                        vm.invoke(it)
                    }
                } else {
                    if (isValidToAdd()) {
                        vm.invoke(
                            TransactionModel(
                                id = "",
                                timestamp = selectedTimeInMillis.toString(),
                                amount = binding.etAmount.text.toString(),
                                upiID = binding.etTitle.text.toString(),
                                sender = "",
                                sms = binding.etTitle.text.toString(),
                                transactionType = if (binding.scExpense.isChecked) "Debit" else "Credit",
                                accountType = binding.etExpenseType.text.toString().trim(),
                                bankName = if (allBankItemsFromDb.isNotEmpty()) allBankItemsFromDb[binding.acsAccounts.selectedItemPosition].bankName else "Self",
                                accountNumber = if (allBankItemsFromDb.isNotEmpty()) allBankItemsFromDb[binding.acsAccounts.selectedItemPosition].accountNumber else "",
                                bankLogoUrl = ""
                            )
                        )
                    } else {
                        return@setOnClickListener
                    }
                }
            }
            dialog?.dismiss()
        }
    }

    private fun isValidToAdd(): Boolean {
        return if (binding.etDate.text.toString().trim().isEmpty()) {
            binding.etDate.error = "Please select Date"
            false
        } else if (binding.etTitle.text.toString().trim().isEmpty()) {
            binding.etTitle.error = "Please enter Description"
            binding.etDate.error = null
            false
        } else if (binding.etAmount.text.toString().trim().isEmpty()) {
            binding.etAmount.error = "Please enter Amount"
            binding.etDate.error = null
            binding.etTitle.error = null
            false
        } else if (binding.etExpenseType.text.toString().trim().isEmpty()) {
            binding.etExpenseType.error = "Please select Expense Type"
            binding.etDate.error = null
            binding.etTitle.error = null
            binding.etAmount.error = null
            false
        } else {
            binding.etDate.error = null
            binding.etTitle.error = null
            binding.etAmount.error = null
            binding.etExpenseType.error = null
            true
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            speechRecognizer.startListening(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startSpeechRecognition()
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        binding.ivAddExpenseMic.visibility = View.GONE
        binding.lavAddExpense.visibility = View.VISIBLE
        binding.lavAddExpense.playAnimation()
    }

    override fun onBeginningOfSpeech() {

    }

    override fun onRmsChanged(rmsdB: Float) {

    }

    override fun onBufferReceived(buffer: ByteArray?) {

    }

    override fun onEndOfSpeech() {
        binding.ivAddExpenseMic.visibility = View.VISIBLE
        binding.lavAddExpense.visibility = View.GONE
        binding.lavAddExpense.cancelAnimation()
    }

    override fun onError(error: Int) {

    }

    private fun showDatePicker(editText: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, monthOfYear, dayOfMonth)
            selectedTimeInMillis = cal.timeInMillis
            editText.setText(Utils.getDateTime(selectedTimeInMillis))
        }, year, month, day)

        //dpd.datePicker.minDate = cal.time.time

        dpd.show()
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val transcribedText = matches[0]
            val amount = extractAmountFromText(transcribedText)
            val title = transcribedText
                .replace("[\\d.]".toRegex(), "")
                .replace("Rs", "", true)
                .replace("rupees", "", true)
                .replace("₹", "")
                .trim()

            binding.etTitle.setText(title)

            if (amount != null) {
                binding.etAmount.setText(amount.toString())
            }

            selectedTimeInMillis = Calendar.getInstance().timeInMillis
            binding.etDate.setText(Utils.getDateTime(selectedTimeInMillis))

            binding.etExpenseType.setText("Cash")
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {

    }

    override fun onEvent(eventType: Int, params: Bundle?) {

    }

    private fun extractAmountFromText(transcribedText: String): Double? {
        val amountPattern = "\\b\\d+(\\.\\d{1,2})?\\b".toRegex()
        val matchResult = amountPattern.find(transcribedText)
        return matchResult?.value?.toDoubleOrNull()
    }


}