package com.example.bevasarlolista.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bevasarlolista.R

class CalculationAdapter(
    private val calculations: List<Pair<String, Double>>
) : RecyclerView.Adapter<CalculationAdapter.CalculationViewHolder>() {

    class CalculationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calculation, parent, false)
        return CalculationViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalculationViewHolder, position: Int) {
        val (month, amount) = calculations[position]
        holder.tvMonth.text = month
        holder.tvAmount.text = "Amount: $amount"
    }

    override fun getItemCount(): Int = calculations.size
}
