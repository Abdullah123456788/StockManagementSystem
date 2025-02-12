package com.example.productmanagement

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Adapter.ItemsAdapter
import com.example.productmanagement.Model.Item
import com.example.productmanagement.Model.ItemsDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Load : AppCompatActivity() {
    private lateinit var time: TextView
    private lateinit var ampm: TextView
    private lateinit var date: TextView
    private lateinit var spinner: Spinner
    private lateinit var save: Button
    private lateinit var db: Button
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var itemList: MutableList<Item>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Load"

        setContentView(R.layout.activity_load)
        time = findViewById(R.id.time)
        ampm = findViewById(R.id.ampm)
        date = findViewById(R.id.date)
        spinner = findViewById(R.id.loaditem)
        save = findViewById(R.id.save)
        db = findViewById(R.id.db)
        editText = findViewById(R.id.edittotalquantity)
        recyclerView = findViewById(R.id.recyclerview)

        itemList = mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this)
        itemsAdapter = ItemsAdapter(itemList, useStorageLayout = false) {
            updateTotalQuantity()
        }
        recyclerView.adapter = itemsAdapter

        save.setOnClickListener {
            saveItemsToDatabase()
            finish()
        }

        db.setOnClickListener {
            loadItemsFromDatabase()
        }

        updateTimeAndDate()
        populateSpinner()
    }
    private fun updateTotalQuantity() {
        val totalQuantity = itemList.sumOf { it.quantity ?: 0 }
        editText.setText(totalQuantity.toString())
    }


    private fun saveItemsToDatabase() {
        lifecycleScope.launch {
            val dao = ItemsDatabase.getDatabase(applicationContext).itemsDao()
            val savedItems = mutableListOf<Item>()

            for (item in itemList) {
                val quantity = item.quantity ?: 0

                if (quantity > 0) {
                    val existingItem = dao.getItemByName(item.items)

                    if (existingItem != null) {
                        existingItem.quantity += quantity
                        existingItem.timestamp = System.currentTimeMillis()
                        dao.updateItem(existingItem)
                        savedItems.add(existingItem)
                    } else {
                        item.timestamp = System.currentTimeMillis()
                        item.quantity = quantity
                        dao.insertItem(item)
                        savedItems.add(item)
                    }
                }
            }

            if (savedItems.isNotEmpty()) {
                Toast.makeText(this@Load, "All items saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@Load, "No new items to save!", Toast.LENGTH_SHORT).show()
            }

            updateTotalQuantity()
        }
    }



    private fun loadItemsFromDatabase() {
        lifecycleScope.launch {
            val dao = ItemsDatabase.getDatabase(applicationContext).itemsDao()
            val allItems = dao.getAllItems()

            Log.d("LoadActivity", "Loaded items: $allItems")

            itemList.clear()
            itemList.addAll(allItems)

            recyclerView.adapter?.notifyDataSetChanged()
            updateTotalQuantity()
        }
    }

    private fun updateTimeAndDate() {
        val handler = Handler()
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

    private fun populateSpinner() {
        val loadArray = resources.getStringArray(R.array.Load)
        val adapter = ArrayAdapter(this@Load, android.R.layout.simple_spinner_item, loadArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@Load, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                itemList.clear()
                val newItems = when (selectedItem) {
                    "Islamabad I9" -> mutableListOf(
                        Item(items = "Pepsi", quantity = 0, timestamp = 0),
                        Item(items = "Coke", quantity = 0, timestamp = 0),
                        Item(items = "Juice", quantity = 0, timestamp = 0),
                        Item(items = "Milk", quantity = 0, timestamp = 0),
                    )
                    "Islamabad I10" -> mutableListOf(
                        Item(items = "Flour", quantity = 0, timestamp = 0),
                        Item(items = "Rice", quantity = 0, timestamp = 0),
                        Item(items = "Beans", quantity = 0, timestamp = 0)
                    )
                    "Karachi Port" -> mutableListOf(
                        Item(items = "Mobiles", quantity = 0, timestamp = 0),
                        Item(items = "LCD", quantity = 0, timestamp = 0),
                        Item(items = "Imported Shirts", quantity = 0, timestamp = 0)
                    )
                    "Lahore" -> mutableListOf(
                        Item(items = "Clothes", quantity = 0),
                        Item(items = "Shorts", quantity = 0),
                        Item(items = "Pants", quantity = 0)
                    )
                    else -> emptyList()
                }

                for (newItem in newItems) {
                    val existingItem = itemList.find { it.items == newItem.items }

                    if (existingItem != null) {
                        existingItem.quantity += newItem.quantity
                        existingItem.timestamp = System.currentTimeMillis()

                    } else {
                        itemList.add(newItem)
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
                updateTotalQuantity()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
