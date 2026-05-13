package com.example.financeapp.domain

import com.example.financeapp.data.model.Transaction
import com.example.financeapp.util.DateUtils
import com.example.financeapp.util.FormatUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.Calendar

object IntentProcessor {
    private val gson = Gson()

    fun parseIntent(rawJson: String): ChatIntent {
        return try {
            val clean = rawJson.replace("```json", "").replace("```", "").trim()
            val obj = gson.fromJson(clean, JsonObject::class.java)
            ChatIntent(
                intent = obj.get("intent")?.asString ?: Intents.UNKNOWN,
                timeRange = obj.get("time_range")?.asString,
                category = obj.get("category")?.asString,
                limit = obj.get("limit")?.asInt
            )
        } catch (e: Exception) {
            ChatIntent(intent = Intents.UNKNOWN)
        }
    }

    fun processIntent(intent: ChatIntent, allTransactions: List<Transaction>): String {
        val range = intent.timeRange ?: TimeRanges.THIS_MONTH
        val filtered = filterByTimeRange(allTransactions, range)
        val rangeLabel = getLabel(range)

        return when (intent.intent) {
            Intents.TOTAL_EXPENSE -> {
                val total = filtered.filter { it.type == "expense" }.sumOf { it.amount }
                "Your total expenses $rangeLabel: ${FormatUtils.formatCurrency(total)}"
            }
            Intents.TOTAL_INCOME -> {
                val total = filtered.filter { it.type == "income" }.sumOf { it.amount }
                "Your total income $rangeLabel: ${FormatUtils.formatCurrency(total)}"
            }
            Intents.HIGHEST_SPENDING_CATEGORY -> {
                val expenses = filtered.filter { it.type == "expense" }
                if (expenses.isEmpty()) return "No expenses found for this period."
                val top = expenses.groupBy { it.category }
                    .mapValues { e -> e.value.sumOf { it.amount } }
                    .maxByOrNull { it.value }
                "Your top spending category $rangeLabel is ${top?.key} — ${FormatUtils.formatCurrency(top?.value ?: 0.0)}"
            }
            Intents.CATEGORY_EXPENSE -> {
                val cat = intent.category ?: return "Which category would you like to check?"
                val total = filtered.filter {
                    it.type == "expense" && it.category.equals(cat, ignoreCase = true)
                }.sumOf { it.amount }
                "You spent ${FormatUtils.formatCurrency(total)} on $cat $rangeLabel."
            }
            Intents.BIGGEST_EXPENSE -> {
                val biggest = filtered.filter { it.type == "expense" }.maxByOrNull { it.amount }
                if (biggest != null) {
                    "Your biggest expense ${getLabel(range)}: ${FormatUtils.formatCurrency(biggest.amount)} on ${biggest.category}" +
                            if (biggest.note.isNotEmpty()) " (${biggest.note})" else ""
                } else "No expenses found."
            }
            Intents.BALANCE -> {
                val income = filtered.filter { it.type == "income" }.sumOf { it.amount }
                val expense = filtered.filter { it.type == "expense" }.sumOf { it.amount }
                "Balance $rangeLabel\n" +
                        "Income:   ${FormatUtils.formatCurrency(income)}\n" +
                        "Expenses: ${FormatUtils.formatCurrency(expense)}\n" +
                        "Net:      ${FormatUtils.formatCurrency(income - expense)}"
            }
            Intents.TRANSACTION_COUNT -> {
                "You have ${filtered.size} transaction(s) $rangeLabel."
            }
            Intents.RECENT_TRANSACTIONS -> {
                val limit = intent.limit ?: 5
                val recent = filtered.sortedByDescending { it.timestamp }.take(limit)
                if (recent.isEmpty()) return "No transactions found."
                "Recent ${recent.size} transactions:\n" +
                        recent.joinToString("\n") { "• ${it.category}: ${FormatUtils.formatCurrency(it.amount)} (${it.date})" }
            }
            else -> "I couldn't understand that. Try asking:\n• How much did I spend this month?\n• What's my top spending category?\n• Show my last 5 transactions."
        }
    }

    private fun filterByTimeRange(transactions: List<Transaction>, range: String): List<Transaction> {
        val now = System.currentTimeMillis() / 1000
        return when (range) {
            TimeRanges.THIS_MONTH -> {
                val (s, e) = DateUtils.getCurrentMonthRange()
                transactions.filter { it.timestamp in s..e }
            }
            TimeRanges.LAST_MONTH -> {
                val (s, e) = DateUtils.getMonthRange(1)
                transactions.filter { it.timestamp in s..e }
            }
            TimeRanges.THIS_YEAR -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_YEAR, 1); cal.set(Calendar.HOUR_OF_DAY, 0)
                val yearStart = cal.timeInMillis / 1000
                transactions.filter { it.timestamp in yearStart..now }
            }
            else -> transactions
        }
    }

    private fun getLabel(range: String?): String = when (range) {
        TimeRanges.THIS_MONTH -> "this month"
        TimeRanges.LAST_MONTH -> "last month"
        TimeRanges.THIS_YEAR -> "this year"
        TimeRanges.ALL_TIME -> "overall"
        else -> "this month"
    }
}