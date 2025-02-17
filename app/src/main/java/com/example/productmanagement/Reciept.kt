package com.example.productmanagement

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Adapter.ExpenseAdapter
import com.example.productmanagement.Model.ExpenseDatabase
import com.example.productmanagement.Model.ExpenseItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Reciept : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalExpenseText: TextView
    private lateinit var database: ExpenseDatabase
    private val expenseList = mutableListOf<ExpenseItem>()
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reciept)
        supportActionBar?.title = "Receipt For All Expanses"

        recyclerView = findViewById(R.id.receipt_recyclerView)
        totalExpenseText = findViewById(R.id.total_expense_receipt)

        database = ExpenseDatabase.getDatabase(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        expenseAdapter = ExpenseAdapter(expenseList)
        recyclerView.adapter = expenseAdapter

        loadExpenses()
    }

    private fun loadExpenses() {
        lifecycleScope.launch(Dispatchers.IO) {
            val expenses = database.expenseDao().getAllExpenses()
            withContext(Dispatchers.Main) {
                if (expenses.isNotEmpty()) {
                    expenseList.clear()
                    expenseList.addAll(expenses)
                    expenseAdapter.notifyDataSetChanged()
                    updateTotalExpense()
                } else {
                    Toast.makeText(this@Reciept, "No expenses found!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTotalExpense() {
        val total = expenseList.sumOf { it.amount }
        totalExpenseText.text = "Total: Rs. $total"
    }
}
