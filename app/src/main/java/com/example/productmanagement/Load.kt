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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
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

        fetchAllDataFromFirebase()

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

    private val locationItemsMap = mutableMapOf<String, List<String>>()

    private fun fetchAllDataFromFirebase() {
        val database = FirebaseFirestore.getInstance().collection("Location")

        database.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val locationName = document.getString("name") ?: continue
                val items = document.get("items") as? List<String> ?: emptyList()
                locationItemsMap[locationName] = items
            }
            Log.d("FirebaseData", "All locations & items loaded successfully")
        }.addOnFailureListener { exception ->
            Log.e("FirebaseError", "Error fetching locations: ${exception.message}")
        }
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
//    private fun getnames()
//    {
//        val database = FirebaseFirestore.getInstance().collection("Location")
//        database.whereEqualTo("name", selectedItem).get()
//    }


    private fun populateSpinner() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val loadArray = resources.getStringArray(R.array.Load)
        val adapter = ArrayAdapter(this@Load, android.R.layout.simple_spinner_item, loadArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@Load, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()

                itemList.clear()

                val items = locationItemsMap[selectedItem] ?: emptyList()

                val newItems = items.map { itemName ->
                    Item(items = itemName, distributionQuantity = 0, quantity = 0, timestamp = 0, userId = userId)
                }

                itemList.addAll(newItems)
                recyclerView.adapter?.notifyDataSetChanged()
                updateTotalQuantity()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

    }
}
