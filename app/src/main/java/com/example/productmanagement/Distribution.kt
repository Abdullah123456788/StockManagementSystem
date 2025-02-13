package com.example.productmanagement

import android.os.Bundle
import android.os.Handler
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Adapter.DistributionAdapter
import com.example.productmanagement.Model.DistributeRecordItems
import com.example.productmanagement.Model.Item
import com.example.productmanagement.Model.ItemsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Distribution : AppCompatActivity() {
    private val handler = Handler()
    private lateinit var time: TextView
    private lateinit var ampm: TextView
    private lateinit var date: TextView
    lateinit var locationSpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var saveButton: Button
    private lateinit var itemList: MutableList<Item>
    private lateinit var distributionAdapter: DistributionAdapter
    private var selectedLocation: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distribution)
        supportActionBar?.title = "Distribution"

        time = findViewById(R.id.time)
        ampm = findViewById(R.id.ampm)
        date = findViewById(R.id.date)
        locationSpinner = findViewById(R.id.distribution)
        recyclerView = findViewById(R.id.recyclerview)
        saveButton = findViewById(R.id.save)

        itemList = mutableListOf()
        distributionAdapter = DistributionAdapter(this, itemList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = distributionAdapter

        loadItemsFromDatabase()

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.Distribution,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = adapter

        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedLocation = parent.getItemAtPosition(position).toString()
                Toast.makeText(this@Distribution, "Selected: $selectedLocation", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        saveButton.setOnClickListener {
            saveDistributionRecords()
        }

        updateTimeAndDate()
    }

    private fun loadItemsFromDatabase() {
        lifecycleScope.launch {
            val dao = ItemsDatabase.getDatabase(applicationContext).itemsDao()
            val allItems = dao.getAllItems()

            itemList.clear()
            itemList.addAll(allItems)

            distributionAdapter.notifyDataSetChanged()
        }
    }

    private fun saveDistributionRecords() {
        val updatedRecords = distributionAdapter.getDistributionRecords()
        val db = ItemsDatabase.getDatabase(applicationContext)
        val itemsDao = db.itemsDao()
        val distributionDao = db.DistributionDao()

        lifecycleScope.launch(Dispatchers.IO) {
            updatedRecords.forEach { item ->
                if (item.distributionQuantity > 0) {
                    val newTimestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)

                    val distributionRecord = DistributeRecordItems(
                        item = item.items,
                        timestamp = newTimestamp,
                        distributionquantity = item.distributionQuantity,
                        location = selectedLocation
                    )

                    distributionDao.insertDistribution(distributionRecord)
                    itemsDao.updateItem(item)
                    finish()
                }
            }

            runOnUiThread {
                Toast.makeText(applicationContext, "Items and Distribution records saved!", Toast.LENGTH_SHORT).show()
                loadItemsFromDatabase()
            }
        }
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
