package com.example.financeapp.presentation.addtransaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class AddTransactionViewModel : ViewModel() {
    private val repository = TransactionRepository()

    private val _result = MutableLiveData<Boolean>()
    val addResult: LiveData<Boolean> = _result

    private val _loading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _loading

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _loading.value = true
            _result.value = repository.addTransaction(transaction).isSuccess
            _loading.value = false
        }
    }
}