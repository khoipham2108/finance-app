package com.example.financeapp.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financeapp.R
import com.example.financeapp.databinding.FragmentDashboardBinding
import com.example.financeapp.presentation.history.TransactionAdapter
import com.example.financeapp.util.DateUtils
import com.example.financeapp.util.FormatUtils
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter { /* no action on dashboard tap */ }
        binding.rvRecentTransactions.apply {
            this.adapter = this@DashboardFragment.adapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }

        val user = FirebaseAuth.getInstance().currentUser
        binding.tvGreeting.text = "Hello, ${user?.displayName?.split(" ")?.firstOrNull() ?: "there"} 👋"
        binding.tvMonthYear.text = DateUtils.getCurrentMonthYear()

        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.take(5))
        }
        viewModel.totalExpenseThisMonth.observe(viewLifecycleOwner) {
            binding.tvTotalExpense.text = FormatUtils.formatCurrency(it)
        }
        viewModel.totalIncomeThisMonth.observe(viewLifecycleOwner) {
            binding.tvTotalIncome.text = FormatUtils.formatCurrency(it)
        }
        viewModel.balanceThisMonth.observe(viewLifecycleOwner) {
            binding.tvBalance.text = FormatUtils.formatCurrency(it)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        binding.fabAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_addTransaction)
        }
        binding.tvSeeAll.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_history)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}