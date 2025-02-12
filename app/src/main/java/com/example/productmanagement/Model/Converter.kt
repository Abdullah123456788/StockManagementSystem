package com.example.productmanagement.Model

import androidx.room.TypeConverter
import com.example.productmanagement.Model.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {
    @TypeConverter
    fun fromItemList(value: MutableList<Item>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toItemList(value: String): MutableList<Item> {
        val listType = object : TypeToken<MutableList<Item>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
