package com.example.productmanagement.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.productmanagement.R
import com.example.productmanagement.Model.Item
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemsAdapter(private var itemList: MutableList<Item>, private val useStorageLayout: Boolean , private val onQuantityChanged:()->Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM_LAYOUT = 1
        private const val TYPE_STORAGE_LAYOUT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (useStorageLayout) TYPE_STORAGE_LAYOUT else TYPE_ITEM_LAYOUT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_STORAGE_LAYOUT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_storage, parent, false)
                StorageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = itemList[position]
        val formattedTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(currentItem.timestamp))

        when (holder) {
            is ItemViewHolder -> {
                holder.itemName.text = currentItem.items
                holder.itemQuantity.text = currentItem.quantity.toString()
                holder.btnPlus.setOnClickListener {
                    currentItem.quantity++
                    currentItem.timestamp = System.currentTimeMillis()

                    Log.d("Timme", "after update: ${currentItem}")

                    holder.itemQuantity.text = currentItem.quantity.toString()
                    onQuantityChanged()
                }

                holder.btnMinus.setOnClickListener {
                    if (currentItem.quantity > 0) {
                        currentItem.quantity--
                        currentItem.timestamp = System.currentTimeMillis()

                        holder.itemQuantity.text = currentItem.quantity.toString()
                        onQuantityChanged()
                    }
                }
            }

            is StorageViewHolder -> {
                holder.timestampTextView.text = "Time: $formattedTime"
                holder.itemName.text = currentItem.items
                holder.itemQuantity.text = currentItem.quantity.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun updateData(newList: MutableList<Item>) {
        itemList = newList
        notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemname)
        val itemQuantity: TextView = itemView.findViewById(R.id.itemcalculate)
        val btnPlus: Button = itemView.findViewById(R.id.plus)
        val btnMinus: Button = itemView.findViewById(R.id.minus)
    }

    class StorageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timestampTextView: TextView=itemView.findViewById(R.id.tvitemtime)
        val itemName: TextView = itemView.findViewById(R.id.tvitemname)
        val itemQuantity: TextView = itemView.findViewById(R.id.tvitemquantity)
    }
}
