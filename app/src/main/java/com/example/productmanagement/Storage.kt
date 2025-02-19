package com.example.productmanagement

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Adapter.ItemsAdapter
import com.example.productmanagement.Model.Item
import com.example.productmanagement.Model.ItemsDao
import com.example.productmanagement.Model.ItemsDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Storage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tableLayout: TableLayout
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var itemsDao: ItemsDao
    private lateinit var time: TextView
    private val handler = Handler()

    private var itemList: MutableList<Item> = mutableListOf()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        supportActionBar?.title = "All Loaded Items"

        recyclerView = findViewById(R.id.storagerecyclerview)
        tableLayout = findViewById(R.id.tableLayout)
        time = findViewById(R.id.tvitemtime)

        recyclerView.layoutManager = LinearLayoutManager(this)
        itemsAdapter = ItemsAdapter(itemList, useStorageLayout = true)
        {

        }
        recyclerView.adapter = itemsAdapter

        val database = ItemsDatabase.getDatabase(this)
        itemsDao = database.itemsDao()

        loadItemsFromDatabase()
    }
//    private fun updateDateTime() {
//        handler.post(object : Runnable {
//            override fun run() {
//                val calendar = Calendar.getInstance()
//                val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
//                time.text = timeFormat.format(calendar.time)
//                handler.postDelayed(this, 1000)
//            }
//        })
//    }

    private fun loadItemsFromDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            val itemsList = itemsDao.getAllItems(userId) as MutableList
            withContext(Dispatchers.Main) {
                itemsAdapter.updateData(itemsList)
                addItemsToTable(itemsList)
            }
        }
    }

    private fun addItemsToTable(itemsList: List<Item>) {
        tableLayout.removeAllViews()

        val headerRow = TableRow(this).apply {
            setPadding(5, 5, 5, 5)
            setBackgroundResource(R.color.Green)
        }

        val headerItem = TextView(this).apply {
            text = "Item"
            setTextColor(getColor(R.color.white))
            textSize = 18f
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        }

        val headerQuantity = TextView(this).apply {
            text = "Quantity"
            setTextColor(getColor(R.color.white))
            textSize = 18f
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        }

        val headerTime = TextView(this).apply {
            text = "Time"
            setTextColor(getColor(R.color.white))
            textSize = 18f
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        }

        headerRow.addView(headerItem)
        headerRow.addView(headerQuantity)
        headerRow.addView(headerTime)
        tableLayout.addView(headerRow)

        val headerSeparator = View(this).apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2)
            setBackgroundColor(getColor(R.color.Gray))
        }
        tableLayout.addView(headerSeparator)

        for (item in itemsList) {
            val row = TableRow(this).apply {
                setPadding(5, 5, 5, 5)
                setBackgroundResource(R.color.white)
            }

            val itemName = TextView(this).apply {
                id = View.generateViewId()
                text = item.items
                gravity = Gravity.CENTER
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            }

            val itemQuantity = TextView(this).apply {
                id = View.generateViewId()
                text = item.quantity?.toString()
                gravity = Gravity.CENTER
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            }

            val itemTime = TextView(this).apply {
                id = View.generateViewId()
                text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(item.timestamp))
                gravity = Gravity.CENTER
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            }

            row.addView(itemName)
            row.addView(itemQuantity)
            row.addView(itemTime)
            tableLayout.addView(row)

            val separator = View(this).apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1)
                setBackgroundColor(getColor(R.color.Gray))
            }
            tableLayout.addView(separator)
        }
    }



}
