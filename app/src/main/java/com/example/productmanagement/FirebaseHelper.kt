package com.example.productmanagement.Firebase

import android.util.Log
import com.example.productmanagement.Model.DistributeRecordItems
import com.example.productmanagement.Model.Item
import com.example.productmanagement.Model.ExpenseItem
import com.example.productmanagement.Model.Location
import com.example.productmanagement.Model.Stock
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseHelper {
    private val firestore = FirebaseFirestore.getInstance()

    fun uploadDistributionsToFirebase(distributionList: List<DistributeRecordItems>, callback: (Boolean) -> Unit) {
        val batch = firestore.batch()

        distributionList.forEach { item ->
            val docRef = firestore.collection("distributions").document(item.id.toString())
            batch.set(docRef, item)
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d("FirebaseHelper", "Distributions uploaded successfully")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseHelper", "Error uploading distributions", e)
                callback(false)
            }
    }

    fun uploadItemsToFirebase(itemsList: List<Item>, callback: (Boolean) -> Unit) {
        val batch = firestore.batch()

        itemsList.forEach { item ->
            val docRef = firestore.collection("Stock").document(item.id.toString())
            batch.set(docRef, item)
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d("FirebaseHelper", "Items uploaded successfully")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseHelper", "Error uploading items", e)
                callback(false)
            }
    }

    fun uploadLocationtodb(Location: List<Location>, callback: (Boolean) -> Unit) {
        val batch = firestore.batch()

        Location.forEach { item ->
            val docRef = firestore.collection("Location").document(item.id.toString())
            batch.set(docRef, item)
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d("FirebaseHelper", "Location uploaded successfully")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseHelper", "Error uploading items", e)
                callback(false)
            }
    }

    fun uploadStocktodatabase(itemsList: List<Stock>, callback: (Boolean) -> Unit) {
        val batch = firestore.batch()

        itemsList.forEach { item ->
            val docRef = firestore.collection("Load").document(item.id.toString())
            batch.set(docRef, item)
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d("FirebaseHelper", "Stocks uploaded successfully")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseHelper", "Error uploading items", e)
                callback(false)
            }
    }

    fun uploadExpensesToFirebase(expenseList: List<ExpenseItem>, callback: (Boolean) -> Unit) {
        val batch = firestore.batch()

        expenseList.forEach { expense ->
            val docRef = firestore.collection("expenses").document(expense.id.toString())
            batch.set(docRef, expense)
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d("FirebaseHelper", "Expenses uploaded successfully")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseHelper", "Error uploading expenses", e)
                callback(false)
            }
    }
}
