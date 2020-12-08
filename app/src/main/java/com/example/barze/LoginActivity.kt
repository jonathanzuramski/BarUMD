package com.example.barze

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

//Login Activity inspired by tutorial found at: https://medium.com/@sfazleyrabbi/firebase-login-and-registration-authentication-99ea25388cbf
class LoginActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    lateinit var emailText : EditText
    lateinit var passText : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //checks if user is logged in
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.button)
        val registerButton = findViewById<Button>(R.id.button2)
        emailText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        passText = findViewById<EditText>(R.id.editTextTextPassword)

        //Button listeners for Login and Register
        loginButton.setOnClickListener{
            loginUser();
        }

        registerButton.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }


    }

    private fun loginUser() {
        val email = emailText.text.toString()
        val password = passText.text.toString()

        //checks if user is registered
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = firebaseAuth!!.uid
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("uid",uid)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@LoginActivity, "Authentication was not achieved.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter data in all fields", Toast.LENGTH_SHORT).show()
        }
    }


}