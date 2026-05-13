package com.example.financeapp.data.model

import com.google.firebase.firestore.DocumentId

data class Transaction(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val note: String = "",
    val date: String = "",
    val paymentMethod: String = "",
    val type: String = "expense",   // "expense" | "income"
    val timestamp: Long = 0L
)