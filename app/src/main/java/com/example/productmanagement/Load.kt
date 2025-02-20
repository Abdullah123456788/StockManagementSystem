package com.example.productmanagement

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Adapter.ItemsAdapter
import com.example.productmanagement.Model.Item
import com.example.productmanagement.Model.ItemsDatabase
import com.example.productmanagement.Model.Location
import com.example.productmanagement.Model.Stock
import com.google.firebase.auth.FirebaseAuth
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
    private var selectedlocation :String =""
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

        db.visibility =View.GONE

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
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val dao = ItemsDatabase.getDatabase(applicationContext).itemsDao()
            val stockDao = ItemsDatabase.getDatabase(applicationContext).stockDao()
            val LocationDao = ItemsDatabase.getDatabase(applicationContext).locationDao()
            val savedItems = mutableListOf<Item>()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            lifecycleScope.launch {
                val db = ItemsDatabase.getDatabase(applicationContext)
                val locations = listOf(
                    Location(name = "Islamabad I9"),
                    Location(name = "Islamabad I10"),
                    Location(name = "Karachi Port"),
                    Location(name = "Lahore")
                )

                for (location in locations) {
                    val existingLocation = db.locationDao().getLocationByName(location.name)
                    if (existingLocation == null) {
                        db.locationDao().insertLocation(location)
                    }
                }
            }


            selectedlocation = spinner.selectedItem?.toString() ?: ""

            for (item in itemList) {
                val quantity = item.quantity ?: 0

                if (quantity > 0) {
                    val formattedTime = dateFormat.format(Date(System.currentTimeMillis()))
                    val existingItem = dao.getItemByName(item.items, userId = userId)

                    if (existingItem != null) {
                        existingItem.quantity += quantity
                        existingItem.timestamp = System.currentTimeMillis()
                        dao.updateItem(existingItem)

                        stockDao.updateStockItem(existingItem.items, quantity, existingItem.timestamp.toString())

                        savedItems.add(existingItem)
                    } else {
                        item.timestamp = System.currentTimeMillis()
                        item.quantity = quantity
                        dao.insertItem(item)

                        val stock = Stock(
                            item = item.items,
                            quantity = item.quantity,
                            location = selectedlocation,
                            timestamp = formattedTime
                        )
                        stockDao.insertStock(stock)

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
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val dao = ItemsDatabase.getDatabase(applicationContext).itemsDao()
            val allItems = dao.getAllItems(userId = userId)

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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
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
                        Item(items = "Pepsi", distributionQuantity = 0, quantity = 0, timestamp = 0, userId = userId),
                        Item(items = "Coke", distributionQuantity = 0,quantity = 0, timestamp = 0, userId = userId),
                        Item(items = "Juice",distributionQuantity = 0, quantity = 0, timestamp = 0, userId = userId),
                        Item(items = "Milk", distributionQuantity = 0,quantity = 0, timestamp = 0, userId = userId),
                    )
                    "Islamabad I10" -> mutableListOf(
                        Item(items = "Flour",distributionQuantity = 0, quantity = 0, timestamp = 0, userId = userId),
                        Item(items = "Rice",distributionQuantity = 0, quantity = 0, timestamp = 0, userId = userId),
                        Item(items = "Beans",distributionQuantity = 0, quantity = 0, timestamp = 0, userId = userId)
                    )
                    "Karachi Port" -> mutableListOf(
                        Item(items = "Mobiles", distributionQuantity = 0,quantity = 0, timestamp = 0, userId = userId),
                        Item(items = "LCD", distributionQuantity = 0,quantity = 0, timestamp = 0, userId = userId),
                        Item(items = "Imported Shirts",distributionQuantity = 0, quantity = 0, timestamp = 0, userId = userId)
                    )
                    "Lahore" -> mutableListOf(
                        Item(items = "Clothes", distributionQuantity = 0,quantity = 0, userId = userId),
                        Item(items = "Shorts",distributionQuantity = 0, quantity = 0, userId = userId),
                        Item(items = "Pants", distributionQuantity = 0,quantity = 0, userId = userId)
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
