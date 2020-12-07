package com.example.barze

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    lateinit var emailText : EditText
    lateinit var passText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("uid",firebaseAuth!!.uid)
            startActivity(intent)
            finish()
        }
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.button)
        val registerButton = findViewById<Button>(R.id.button2)
        emailText = findViewById<EditText>(R.id.editTextTextEmailAddress)
        passText = findViewById<EditText>(R.id.editTextTextPassword)




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

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            Log.d("TAG", "Logging in user.")
            firebaseAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "signInWithEmail:success")
                        val uid = firebaseAuth!!.uid
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("uid",uid)
                        startActivity(intent)
                    } else {
                        Log.e("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }


}