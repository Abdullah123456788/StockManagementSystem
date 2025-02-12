package com.example.productmanagement.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.Model.DistributeRecordItems
import com.example.productmanagement.R

class DistributionRecordAdapter(
    private val context: Context,
    private val list: MutableList<DistributeRecordItems>
) : RecyclerView.Adapter<DistributionRecordAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val distributionTime: TextView = view.findViewById(R.id.distributionTime)
        val itemName: TextView = view.findViewById(R.id.distributeitem)
        val quantity: TextView = view.findViewById(R.id.distributeitemqunatity)
        val location: TextView = view.findViewById(R.id.Location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.distribute_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = list[position]
        holder.distributionTime.text = record.timestamp.toString() // Convert timestamp properly if needed
        holder.itemName.text = record.item
        holder.quantity.text = record.distributionquantity.toString()
        holder.location.text = record.location
    }

    fun updateList(newList: List<DistributeRecordItems>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
