package com.example.financeapp.data.repository

import com.example.financeapp.BuildConfig
import com.example.financeapp.data.remote.GeminiContent
import com.example.financeapp.data.remote.GeminiPart
import com.example.financeapp.data.remote.GeminiRequest
import com.example.financeapp.data.remote.RetrofitClient

class ChatRepository {
    private val api = RetrofitClient.geminiApiService

    // The system prompt instructs Gemini to return ONLY structured JSON intents.
    // The app then processes the intent safely — Gemini never touches Firestore directly.
    private val systemPrompt = """
You are a finance query parser for a personal expense tracking app.
When the user asks a finance-related question, respond ONLY with a JSON object.
No explanation. No markdown. No extra text. Just the JSON.

Supported intents:
- total_expense       → user wants total spending
- total_income        → user wants total income
- highest_spending_category → which category was spent most
- category_expense    → spending in a specific category (include "category" field)
- biggest_expense     → the largest single expense
- balance             → income minus expense
- transaction_count   → how many transactions
- recent_transactions → show recent list (include "limit" field, default 5)
- unknown             → cannot understand the question

Supported time_range values (default: this_month):
  this_month | last_month | this_year | all_time

Example outputs:
{"intent":"total_expense","time_range":"this_month"}
{"intent":"category_expense","time_range":"this_month","category":"Food"}
{"intent":"highest_spending_category","time_range":"this_month"}
{"intent":"biggest_expense","time_range":"all_time"}
{"intent":"balance","time_range":"last_month"}
{"intent":"recent_transactions","limit":5}
{"intent":"unknown"}

Respond with ONLY the JSON object. Nothing else.
    """.trimIndent()

    suspend fun parseUserIntent(userMessage: String): Result<String> {
        return try {
            val prompt = "$systemPrompt\n\nUser question: $userMessage"
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(prompt)))
                )
            )
            val response = api.generateContent(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = request
            )
            val text = response.candidates
                ?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return Result.failure(Exception("Empty Gemini response"))
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}