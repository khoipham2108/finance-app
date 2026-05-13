package com.example.financeapp.presentation.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.financeapp.databinding.FragmentStatisticsBinding
import com.example.financeapp.util.FormatUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.totalExpense.observe(viewLifecycleOwner) {
            binding.tvTotalExpense.text = FormatUtils.formatCurrency(it)
        }
        viewModel.totalIncome.observe(viewLifecycleOwner) {
            binding.tvTotalIncome.text = FormatUtils.formatCurrency(it)
        }
        viewModel.categoryData.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) setupPieChart(data)
        }
        viewModel.monthlyData.observe(viewLifecycleOwner) { data ->
            if (data.isNotEmpty()) setupBarChart(data)
        }
    }

    private fun setupPieChart(data: Map<String, Double>) {
        val palette = listOf(
            Color.parseColor("#1A73E8"), Color.parseColor("#4A90D9"),
            Color.parseColor("#0D47A1"), Color.parseColor("#90CAF9"),
            Color.parseColor("#1565C0")
        )
        val entries = data.entries.mapIndexed { _, (cat, amt) -> PieEntry(amt.toFloat(), cat) }
        val ds = PieDataSet(entries, "").apply {
            colors = palette; valueTextSize = 11f; valueTextColor = Color.WHITE; sliceSpace = 3f
        }
        binding.pieChart.apply {
            this.data = PieData(ds)
            description.isEnabled = false
            isDrawHoleEnabled = true; holeRadius = 38f
            setHoleColor(Color.TRANSPARENT)
            setEntryLabelColor(Color.WHITE); setEntryLabelTextSize(10f)
            legend.textColor = Color.parseColor("#333333")
            animateY(800); invalidate()
        }
    }

    private fun setupBarChart(data: List<Pair<String, Double>>) {
        val entries = data.mapIndexed { i, (_, v) -> BarEntry(i.toFloat(), v.toFloat()) }
        val ds = BarDataSet(entries, "").apply {
            color = Color.parseColor("#1A73E8"); valueTextSize = 9f
        }
        binding.barChart.apply {
            this.data = BarData(ds).apply { barWidth = 0.5f }
            description.isEnabled = false; legend.isEnabled = false
            setFitBars(true)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(data.map { it.first })
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f; setDrawGridLines(false)
                textColor = Color.parseColor("#666666")
            }
            axisLeft.textColor = Color.parseColor("#666666")
            axisRight.isEnabled = false
            animateY(800); invalidate()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}