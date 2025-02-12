package com.example.productmanagement.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Distribution
import com.example.productmanagement.Model.DistributeRecordItems
import com.example.productmanagement.Model.Item
import com.example.productmanagement.Model.ItemsDatabase
import com.example.productmanagement.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DistributionAdapter(private val context: Context, private val list: List<Item>) :
    RecyclerView.Adapter<DistributionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemname)
        val btnplus: Button = view.findViewById(R.id.plus)
        val btnminus: Button = view.findViewById(R.id.minus)
        val inStock: TextView = view.findViewById(R.id.instock)
        val distribution: TextView = view.findViewById(R.id.itemcalculate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.distribution_layout, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        val formattedTime = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date(currentItem.timestamp))

        val db = ItemsDatabase.getDatabase(context)
        val distributionDao = db.DistributionDao()


        GlobalScope.launch(Dispatchers.IO) {
            val existingRecord = distributionDao.getDistributionByItem(currentItem.items)
            val distributionQuantity = existingRecord?.distributionquantity ?: 0

            launch(Dispatchers.Main) {
                holder.distribution.text = distributionQuantity.toString()
            }

        }


        holder.itemName.text = currentItem.items
        holder.inStock.text = currentItem.quantity.toString()

        holder.btnplus.setOnClickListener {
            if (currentItem.quantity > 0) {
                currentItem.quantity--
                holder.inStock.text = currentItem.quantity.toString()

                val activity = context as? Distribution
                val selectedLocation = activity?.locationSpinner?.selectedItem?.toString() ?: "Default Location"

                val newDistributionQuantity = holder.distribution.text.toString().toInt() + 1
                holder.distribution.text = newDistributionQuantity.toString()

                val newTimestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                GlobalScope.launch(Dispatchers.IO) {
                    val existingRecord = distributionDao.getDistributionByItem(currentItem.items)

                    if (existingRecord == null) {
                        val newRecord = DistributeRecordItems(
                            item = currentItem.items,
                            timestamp = newTimestamp,
                            distributionquantity = newDistributionQuantity,
                            location = selectedLocation
                        )
                        distributionDao.insertDistribution(newRecord)
                    } else {
                        distributionDao.updateDistributionQuantity(currentItem.items, newDistributionQuantity, newTimestamp, selectedLocation)
                    }

                    launch(Dispatchers.Main) {
                        list.toMutableList().removeAt(position)
                        notifyDataSetChanged()
                    }
                }
            } else {
                Toast.makeText(holder.itemView.context, "No items in stock", Toast.LENGTH_SHORT).show()
            }
        }



        holder.btnminus.setOnClickListener {
            val distributionCount = holder.distribution.text.toString().toInt()

            if (distributionCount > 0) {
                currentItem.quantity++
                holder.inStock.text = currentItem.quantity.toString()

                val newDistributionQuantity = distributionCount - 1
                holder.distribution.text = newDistributionQuantity.toString()

                val activity = context as? Distribution
                val selectedLocation = activity?.locationSpinner?.selectedItem?.toString() ?: "Default Location"
                val newTimestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                GlobalScope.launch(Dispatchers.IO) {
                    distributionDao.updateDistributionQuantity(
                        currentItem.items,
                        newDistributionQuantity,
                        newTimestamp,
                        selectedLocation
                    )
                }
            }
        }

    }
    }

