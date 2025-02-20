package com.example.productmanagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccount : AppCompatActivity() {
    private lateinit var submit: Button
    private lateinit var alreadyAccount: Button
    private lateinit var email: EditText
    private lateinit var name: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        submit = findViewById(R.id.submit)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressbar)
        confirmPassword = findViewById(R.id.confirmpassword)
        alreadyAccount = findViewById(R.id.alreadyaccount)
        progressBar.visibility=View.GONE

        submit.setOnClickListener {
            val emailText = email.text.toString().trim()
            val nameText = name.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val confirmPasswordText = confirmPassword.text.toString().trim()

            if (emailText.isNotEmpty() && nameText.isNotEmpty() && passwordText.isNotEmpty() && confirmPasswordText.isNotEmpty()) {
                if (passwordText == confirmPasswordText) {
                    isEmailRegistered(emailText) { isRegistered ->
                        if (isRegistered) {
                            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                        } else {
                            progressBar.visibility = View.VISIBLE
                            registerUser(nameText, emailText, passwordText)
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }


        alreadyAccount.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }
    private fun isEmailRegistered(email: String, callback: (Boolean) -> Unit) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    callback(signInMethods?.isNotEmpty() == true)
                } else {
                    callback(false)
                }
            }
    }


    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar.visibility=View.GONE
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        saveUserToFirestore(it.uid, name, email)
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserToFirestore(userId: String, name: String, email: String) {
        val user = hashMapOf(
            "userId" to userId,
            "name" to name,
            "email" to email
        )

        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Login::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving user: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
