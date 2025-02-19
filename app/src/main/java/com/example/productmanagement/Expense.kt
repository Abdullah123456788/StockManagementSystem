package com.example.productmanagement

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Adapter.ExpenseAdapter
import com.example.productmanagement.Model.ExpenseDatabase
import com.example.productmanagement.Model.ExpenseItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class Expense : AppCompatActivity() {
    private val handler = Handler()
    private lateinit var time: TextView
    private lateinit var ampm: TextView
    private lateinit var date: TextView
    private lateinit var totalExpense: TextView
    private lateinit var expenseInput: EditText
    private lateinit var spinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnsave: Button
    private lateinit var btndelete: Button
    private lateinit var btnadd: Button
    private lateinit var database: ExpenseDatabase

    private val expenseList = mutableListOf<ExpenseItem>()
    private lateinit var expenseAdapter: ExpenseAdapter

    private var selectedItem: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)
        supportActionBar?.title = "Expense"

        time = findViewById(R.id.time2)
        ampm = findViewById(R.id.ampm)
        date = findViewById(R.id.date)
        totalExpense = findViewById(R.id.totalExpense)
        expenseInput = findViewById(R.id.totalexpenseedit)
        spinner = findViewById(R.id.expanse)
        recyclerView = findViewById(R.id.recyclerview)
        btnsave = findViewById(R.id.btnsave)
        btnadd = findViewById(R.id.btnadd)
        btndelete = findViewById(R.id.btndelete)

        database = ExpenseDatabase.getDatabase(this)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.Expense,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)
        expenseAdapter = ExpenseAdapter(expenseList)
        recyclerView.adapter = expenseAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedItem = if (position > 0) parent.getItemAtPosition(position).toString() else null
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedItem = null
            }
        }

        btnadd.setOnClickListener {
            val enteredAmount = expenseInput.text.toString().toIntOrNull() ?: 0

            if (selectedItem != null && enteredAmount > 0) {
                val currentTime = System.currentTimeMillis()
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                val newExpense = ExpenseItem(item = selectedItem!!, amount = enteredAmount, timestamp = currentTime, userId = userId)

                expenseList.add(newExpense)
                expenseAdapter.notifyDataSetChanged()
                updateTotalExpense()
                expenseInput.text.clear()
            } else {
                Toast.makeText(this, "Select an expense category and enter a valid amount!", Toast.LENGTH_SHORT).show()
            }
        }

        btnsave.setOnClickListener {
            if (expenseList.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    database.expenseDao().insertExpenses(expenseList)
                    withContext(Dispatchers.Main) {
                        expenseList.clear()
                        expenseAdapter.notifyDataSetChanged()
                        totalExpense.text = "0"
                        Toast.makeText(this@Expense, "Expenses saved successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "No expenses to save!", Toast.LENGTH_SHORT).show()
            }
        }

        btndelete.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                database.expenseDao().deleteAllExpenses(userId)
                withContext(Dispatchers.Main) {
                    expenseList.clear()
                    expenseAdapter.notifyDataSetChanged()
                    totalExpense.text = "0"
                    Toast.makeText(this@Expense, "All expenses deleted!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        updateTimeAndDate()
    }

    private fun updateTotalExpense() {
        val total = expenseList.sumOf { it.amount }
        totalExpense.text = total.toString()
    }

    private fun updateTimeAndDate() {
        handler.post(object : Runnable {
            override fun run() {
                val calendar = Calendar.getInstance()
                val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
                val amPmFormat = SimpleDateFormat("a", Locale.getDefault())
                val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

                time.text = timeFormat.format(calendar.time)
                ampm.text = amPmFormat.format(calendar.time)
                date.text = dateFormat.format(calendar.time)

                handler.postDelayed(this, 1000)
            }
        })
    }
}
