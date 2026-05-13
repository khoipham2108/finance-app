package com.example.financeapp.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.financeapp.R
import com.example.financeapp.data.model.Transaction
import com.example.financeapp.databinding.ItemTransactionBinding
import com.example.financeapp.util.DateUtils
import com.example.financeapp.util.FormatUtils

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.VH>(DiffCb()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val b: ItemTransactionBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(t: Transaction) {
            b.tvCategory.text = t.category
            b.tvNote.text = t.note.ifEmpty { t.paymentMethod }
            b.tvDate.text = DateUtils.formatDate(t.date)
            b.tvCategoryIcon.text = emoji(t.category)

            val isExpense = t.type == "expense"
            b.tvAmount.text = if (isExpense) "-${FormatUtils.formatCurrency(t.amount)}"
            else "+${FormatUtils.formatCurrency(t.amount)}"
            b.tvAmount.setTextColor(ContextCompat.getColor(
                b.root.context,
                if (isExpense) R.color.expense_red else R.color.income_green
            ))
            b.root.setOnClickListener { onItemClick(t) }
        }
    }

    private fun emoji(category: String) = when (category.lowercase()) {
        "food"           -> "🍜"
        "shopping"       -> "🛍️"
        "transportation" -> "🚗"
        "education"      -> "📚"
        "entertainment"  -> "🎮"
        "income"         -> "💰"
        else             -> "💳"
    }

    class DiffCb : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(o: Transaction, n: Transaction) = o.id == n.id
        override fun areContentsTheSame(o: Transaction, n: Transaction) = o == n
    }
}