package com.example.productmanagement

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var load:Button
    private lateinit var distribution:Button
    private lateinit var expense:Button
    private lateinit var receipt:Button
    private lateinit var storage:Button
    private lateinit var distributiondb:Button

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
    }
}