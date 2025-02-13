package com.example.productmanagement

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.lifecycleScope
import com.example.productmanagement.Firebase.FirebaseHelper
import com.example.productmanagement.Model.DistributeRecordItems
import com.example.productmanagement.Model.DistributionDao
import com.example.productmanagement.Model.ExpenseDao
import com.example.productmanagement.Model.ExpenseDatabase
import com.example.productmanagement.Model.ExpenseItem
import com.example.productmanagement.Model.Item
import com.example.productmanagement.Model.ItemsDao
import com.example.productmanagement.Model.ItemsDatabase
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var load:Button
    private lateinit var distribution:Button
    private lateinit var expense:Button
    private lateinit var receipt:Button
    private lateinit var storage:Button
    private lateinit var distributiondb:Button
    private lateinit var sync:Button
    private lateinit var database: ItemsDatabase
    private lateinit var database2: ExpenseDatabase
    private lateinit var distributionDao: DistributionDao
    private lateinit var itemdao: ItemsDao
    private lateinit var expensedao: ExpenseDao
    private lateinit var firebaseHelper: FirebaseHelper



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Home"

        load=findViewById(R.id.load)
        distribution=findViewById(R.id.distribution)
        expense=findViewById(R.id.expanse)
        receipt=findViewById(R.id.Reciept)
        storage=findViewById(R.id.Storage)
        distributiondb=findViewById(R.id.distributionrecord)
        sync=findViewById(R.id.Sync)
        database = ItemsDatabase.getDatabase(applicationContext)
        database2 = ExpenseDatabase.getDatabase(applicationContext)
        distributionDao = database.DistributionDao()
        itemdao = database.itemsDao()
        expensedao = database2.expenseDao()
        distributionDao = database.DistributionDao()
        firebaseHelper = FirebaseHelper()
        FirebaseApp.initializeApp(this)


        load.setOnClickListener{
            val intent=Intent(this,Load::class.java)
            startActivity(intent)
        }

        distribution.setOnClickListener{
            val intent=Intent(this,Distribution::class.java)
            startActivity(intent)
        }
        expense.setOnClickListener{
            val intent=Intent(this,Expense::class.java)
            startActivity(intent)
        }
        receipt.setOnClickListener{
            val intent=Intent(this,Reciept::class.java)
            startActivity(intent)
        }
        storage.setOnClickListener{
            val intent=Intent(this,Storage::class.java)
            startActivity(intent)
        }
        distributiondb.setOnClickListener{
            val intent=Intent(this,DistributionRecord::class.java)
            startActivity(intent)
        }
        sync.setOnClickListener{
            syncDataToFirebase()
        }

    }
    private fun syncDataToFirebase() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val distributionList: List<DistributeRecordItems> = distributionDao.getAllDistributions()
                val itemsList: List<Item> = itemdao.getAllItems()
                val expenseList: List<ExpenseItem> = expensedao.getAllExpenses()

                withContext(Dispatchers.Main) {
                    if (distributionList.isNotEmpty()) {
                        firebaseHelper.uploadDistributionsToFirebase(distributionList) { success ->
                            if (success) {
                                Toast.makeText(applicationContext, "Distributions synced!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(applicationContext, "Distributions sync failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    if (itemsList.isNotEmpty()) {
                        firebaseHelper.uploadItemsToFirebase(itemsList) { success ->
                            if (success) {
                                Toast.makeText(applicationContext, "Items synced!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(applicationContext, "Items sync failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    if (expenseList.isNotEmpty()) {
                        firebaseHelper.uploadExpensesToFirebase(expenseList) { success ->
                            if (success) {
                                Toast.makeText(applicationContext, "Expenses synced!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(applicationContext, "Expenses sync failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    if (distributionList.isEmpty() && itemsList.isEmpty() && expenseList.isEmpty()) {
                        Toast.makeText(applicationContext, "No data to sync!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("FirebaseSync", "Sync error", e)
                }
            }
        }
}
}