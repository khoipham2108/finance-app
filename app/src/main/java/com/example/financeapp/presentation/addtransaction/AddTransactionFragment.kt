package com.example.financeapp.presentation.addtransaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.financeapp.R
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.databinding.FragmentAddTransactionBinding
import com.example.financeapp.util.DateUtils
import java.util.Calendar

class AddTransactionFragment : Fragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTransactionViewModel by viewModels()
    private var selectedDate = DateUtils.getCurrentDate()
    private var transactionType = "expense"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Category spinner
        binding.spinnerCategory.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item,
            listOf("Food", "Shopping", "Transportation", "Education", "Entertainment")
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Payment method spinner
        binding.spinnerPaymentMethod.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item,
            listOf("Cash", "Card", "Bank Transfer", "E-wallet")
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Date
        binding.tvSelectedDate.text = selectedDate
        binding.btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                selectedDate = String.format("%d-%02d-%02d", y, m + 1, d)
                binding.tvSelectedDate.text = selectedDate
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Type toggle
        binding.btnExpense.setOnClickListener { setType("expense") }
        binding.btnIncome.setOnClickListener  { setType("income") }
        setType("expense")

        // Save
        binding.btnSave.setOnClickListener { save() }

        viewModel.addResult.observe(viewLifecycleOwner) { ok ->
            if (ok) {
                Toast.makeText(requireContext(), "Transaction saved!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to save", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !loading
        }
    }

    private fun setType(type: String) {
        transactionType = type
        val isExpense = type == "expense"
        binding.btnExpense.setBackgroundResource(
            if (isExpense) R.drawable.bg_toggle_selected else android.R.color.transparent
        )
        binding.btnIncome.setBackgroundResource(
            if (!isExpense) R.drawable.bg_toggle_selected else android.R.color.transparent
        )
        binding.btnExpense.setTextColor(
            resources.getColor(if (isExpense) R.color.text_on_primary else R.color.text_secondary, null)
        )
        binding.btnIncome.setTextColor(
            resources.getColor(if (!isExpense) R.color.text_on_primary else R.color.text_secondary, null)
        )
    }

    private fun save() {
        val amountStr = binding.etAmount.text.toString()
        if (amountStr.isEmpty()) { binding.etAmount.error = "Required"; return }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) { binding.etAmount.error = "Invalid amount"; return }

        viewModel.addTransaction(Transaction(
            amount = amount,
            category = binding.spinnerCategory.selectedItem.toString(),
            note = binding.etNote.text.toString().trim(),
            date = selectedDate,
            paymentMethod = binding.spinnerPaymentMethod.selectedItem.toString(),
            type = transactionType,
            timestamp = System.currentTimeMillis() / 1000
        ))
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}