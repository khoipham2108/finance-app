package com.example.financeapp.domain

data class ChatIntent(
    val intent: String = "",
    val timeRange: String? = null,
    val category: String? = null,
    val limit: Int? = null
)

object Intents {
    const val TOTAL_EXPENSE = "total_expense"
    const val TOTAL_INCOME = "total_income"
    const val HIGHEST_SPENDING_CATEGORY = "highest_spending_category"
    const val CATEGORY_EXPENSE = "category_expense"
    const val BIGGEST_EXPENSE = "biggest_expense"
    const val BALANCE = "balance"
    const val TRANSACTION_COUNT = "transaction_count"
    const val RECENT_TRANSACTIONS = "recent_transactions"
    const val UNKNOWN = "unknown"
}

object TimeRanges {
    const val THIS_MONTH = "this_month"
    const val LAST_MONTH = "last_month"
    const val THIS_YEAR = "this_year"
    const val ALL_TIME = "all_time"
}