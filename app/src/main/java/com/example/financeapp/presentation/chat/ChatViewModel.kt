package com.example.financeapp.presentation.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.ChatMessage
import com.example.financeapp.data.repository.ChatRepository
import com.example.financeapp.data.repository.TransactionRepository
import com.example.financeapp.domain.IntentProcessor
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel : ViewModel() {
    private val chatRepo = ChatRepository()
    private val transactionRepo = TransactionRepository()

    private val _messages = MutableLiveData(mutableListOf<ChatMessage>())
    val messages: LiveData<MutableList<ChatMessage>> = _messages

    private val _loading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _loading

    init {
        addBot("Hi! I'm your finance assistant.\n\nAsk me things like:\n• How much did I spend this month?\n• What's my top category?\n• Show my last 5 transactions.")
    }

    fun sendMessage(text: String) {
        addUser(text)
        viewModelScope.launch {
            _loading.value = true
            try {
                val intentJson = chatRepo.parseUserIntent(text).getOrElse {
                    addBot("Sorry, I couldn't reach the AI service. Please check your connection."); return@launch
                }
                val intent = IntentProcessor.parseIntent(intentJson)
                val transactions = transactionRepo.getAllTransactions()
                val response = IntentProcessor.processIntent(intent, transactions)
                addBot(response)
            } catch (e: Exception) {
                addBot("Something went wrong. Please try again.")
            } finally {
                _loading.value = false
            }
        }
    }

    private fun addUser(text: String) = push(ChatMessage(UUID.randomUUID().toString(), text, true))
    private fun addBot(text: String)  = push(ChatMessage(UUID.randomUUID().toString(), text, false))

    private fun push(msg: ChatMessage) {
        val list = _messages.value ?: mutableListOf()
        list.add(msg)
        _messages.value = list
    }
}