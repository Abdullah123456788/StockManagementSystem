package com.example.productmanagement.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Model.ExpenseItem
import com.example.productmanagement.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseAdapter(private val expenseList: List<ExpenseItem>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        val formattedTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(expense.timestamp))
        holder.timestampTextView.text = "Time: $formattedTime"
        holder.itemTextView.text = expense.item
        holder.amountTextView.text = expense.amount.toString()
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timestampTextView: TextView=view.findViewById(R.id.expensetime)
        val itemTextView: TextView = view.findViewById(R.id.expenseItem)
        val amountTextView: TextView = view.findViewById(R.id.expenseAmount)
    }
}