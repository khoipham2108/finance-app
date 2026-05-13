package com.example.financeapp.presentation.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionHistoryViewModel : ViewModel() {
    private val repository = TransactionRepository()

    private val _all = MutableLiveData<List<Transaction>>()
    private val _filtered = MutableLiveData<List<Transaction>>()
    val filteredTransactions: LiveData<List<Transaction>> = _filtered

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> = _deleteResult

    private var search = ""
    private var category = ""

    init {
        viewModelScope.launch {
            repository.getTransactionsFlow().collect { list ->
                _all.value = list
                applyFilters()
            }
        }
    }

    fun setSearchQuery(q: String) { search = q; applyFilters() }
    fun setCategory(c: String)    { category = c; applyFilters() }

    private fun applyFilters() {
        var result = _all.value ?: emptyList()
        if (search.isNotEmpty())
            result = result.filter { it.note.contains(search, true) || it.category.contains(search, true) }
        if (category.isNotEmpty())
            result = result.filter { it.category == category }
        _filtered.value = result
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            _deleteResult.value = repository.deleteTransaction(id).isSuccess
        }
    }
}