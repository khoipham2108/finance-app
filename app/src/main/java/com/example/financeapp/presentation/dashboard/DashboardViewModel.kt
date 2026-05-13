package com.example.financeapp.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.repository.TransactionRepository
import com.example.financeapp.util.DateUtils
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = TransactionRepository()

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _totalExpense = MutableLiveData(0.0)
    val totalExpenseThisMonth: LiveData<Double> = _totalExpense

    private val _totalIncome = MutableLiveData(0.0)
    val totalIncomeThisMonth: LiveData<Double> = _totalIncome

    private val _balance = MutableLiveData(0.0)
    val balanceThisMonth: LiveData<Double> = _balance

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            repository.getTransactionsFlow().collect { transactions ->
                _isLoading.value = false
                _transactions.value = transactions
                val (start, end) = DateUtils.getCurrentMonthRange()
                val thisMonth = transactions.filter { it.timestamp in start..end }
                _totalExpense.value = thisMonth.filter { it.type == "expense" }.sumOf { it.amount }
                _totalIncome.value = thisMonth.filter { it.type == "income" }.sumOf { it.amount }
                _balance.value = (_totalIncome.value ?: 0.0) - (_totalExpense.value ?: 0.0)
            }
        }
    }
}