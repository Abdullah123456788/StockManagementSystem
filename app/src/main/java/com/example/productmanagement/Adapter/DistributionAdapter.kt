package com.example.productmanagement.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Model.Item
import com.example.productmanagement.R

class DistributionAdapter(private val context: Context, private val itemList: MutableList<Item>) :
    RecyclerView.Adapter<DistributionAdapter.ViewHolder>() {

    // Store distribution quantities separately to save on the "Save" button
    private val distributionMap = mutableMapOf<Int, Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemname)
        val btnPlus: Button = view.findViewById(R.id.plus)
        val btnMinus: Button = view.findViewById(R.id.minus)
        val inStock: TextView = view.findViewById(R.id.instock)
        val distribution: TextView = view.findViewById(R.id.itemcalculate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.distribution_layout, parent, false)
        )
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.itemName.text = currentItem.items
        holder.inStock.text = currentItem.quantity.toString()
        holder.distribution.text = "0"

        if (!distributionMap.containsKey(position)) {
            distributionMap[position] = 0
        }

        holder.btnPlus.setOnClickListener {
            if (currentItem.quantity > 0) {
                currentItem.quantity--
                holder.inStock.text = currentItem.quantity.toString()

                val newDistributionQuantity = (distributionMap[position] ?: 0) + 1
                distributionMap[position] = newDistributionQuantity
                holder.distribution.text = newDistributionQuantity.toString()
            }
        }

        holder.btnMinus.setOnClickListener {
            if ((distributionMap[position] ?: 0) > 0) {
                currentItem.quantity++
                holder.inStock.text = currentItem.quantity.toString()

                val newDistributionQuantity = (distributionMap[position] ?: 0) - 1
                distributionMap[position] = newDistributionQuantity
                holder.distribution.text = newDistributionQuantity.toString()
            }
        }
    }

    fun getDistributionRecords(): List<Item> {
        return itemList.mapIndexed { index, item ->
            item.copy(quantity = item.quantity, distributionQuantity = distributionMap[index] ?: 0)
        }
    }
}
