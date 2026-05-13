package com.example.financeapp.util

import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        formatter.maximumFractionDigits = 0
        return "₫${formatter.format(amount)}"
    }
}