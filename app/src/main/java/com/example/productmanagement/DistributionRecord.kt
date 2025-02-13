package com.example.productmanagement

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Adapter.DistributionRecordAdapter
import com.example.productmanagement.Model.DistributeRecordItems
import com.example.productmanagement.Model.ItemsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DistributionRecord : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DistributionRecordAdapter
    private var recordList: MutableList<DistributeRecordItems> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_distribution_record)
        supportActionBar?.title = "Distribute Items"

        recyclerView = findViewById(R.id.distributerecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DistributionRecordAdapter(this, recordList)
        recyclerView.adapter = adapter

        loadItemsFromDatabase()
    }

    private fun loadItemsFromDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = ItemsDatabase.getDatabase(applicationContext).DistributionDao()
            val allDistributions = dao.getAllDistributions()

            Log.d("Distribution Record", "Loaded items: $allDistributions")

            withContext(Dispatchers.Main) {
                recordList.clear()
                recordList.addAll(allDistributions)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
