package com.example.financeapp.presentation.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeapp.databinding.FragmentHistoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TransactionHistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionHistoryViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter { t ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Transaction")
                .setMessage("Delete \"${t.category}\" — ${t.note.ifEmpty { t.paymentMethod }}?")
                .setPositiveButton("Delete") { _, _ -> viewModel.deleteTransaction(t.id) }
                .setNegativeButton("Cancel", null)
                .show()
        }
        binding.rvTransactions.apply {
            this.adapter = this@TransactionHistoryFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { viewModel.setSearchQuery(s?.toString() ?: "") }
        })

        val categories = listOf("All", "Food", "Shopping", "Transportation", "Education", "Entertainment")
        binding.spinnerCategory.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                viewModel.setCategory(if (pos == 0) "" else categories[pos])
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        viewModel.filteredTransactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.deleteResult.observe(viewLifecycleOwner) { success ->
            if (!success) Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}