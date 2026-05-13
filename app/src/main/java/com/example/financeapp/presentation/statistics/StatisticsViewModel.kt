package com.example.financeapp.presentation.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.repository.TransactionRepository
import com.example.financeapp.util.DateUtils
import kotlinx.coroutines.launch

class StatisticsViewModel : ViewModel() {
    private val repository = TransactionRepository()

    private val _categoryData = MutableLiveData<Map<String, Double>>()
    val categoryData: LiveData<Map<String, Double>> = _categoryData

    private val _monthlyData = MutableLiveData<List<Pair<String, Double>>>()
    val monthlyData: LiveData<List<Pair<String, Double>>> = _monthlyData

    private val _totalExpense = MutableLiveData(0.0)
    val totalExpense: LiveData<Double> = _totalExpense

    private val _totalIncome = MutableLiveData(0.0)
    val totalIncome: LiveData<Double> = _totalIncome

    init {
        viewModelScope.launch {
            repository.getTransactionsFlow().collect { transactions ->
                process(transactions)
            }
        }
    }

    private fun process(transactions: List<Transaction>) {
        val (start, end) = DateUtils.getCurrentMonthRange()
        val thisMonth = transactions.filter { it.timestamp in start..end }

        _categoryData.value = thisMonth
            .filter { it.type == "expense" }
            .groupBy { it.category }
            .mapValues { e -> e.value.sumOf { it.amount } }
            .filter { it.value > 0 }

        _totalExpense.value = thisMonth.filter { it.type == "expense" }.sumOf { it.amount }
        _totalIncome.value  = thisMonth.filter { it.type == "income" }.sumOf { it.amount }

        _monthlyData.value = (5 downTo 0).map { ago ->
            val (s, e) = DateUtils.getMonthRange(ago)
            val label = DateUtils.getMonthLabel(ago)
            val total = transactions.filter { it.type == "expense" && it.timestamp in s..e }.sumOf { it.amount }
            Pair(label, total)
        }
    }
}