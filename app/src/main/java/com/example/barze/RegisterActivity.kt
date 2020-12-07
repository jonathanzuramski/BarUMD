package com.example.barze

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    lateinit var registerButton : Button
    lateinit var firstNameText : EditText
    lateinit var lastNameText : EditText
    lateinit var emailText : EditText
    lateinit var passwordText : EditText
    private lateinit var usersDatabaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton = findViewById(R.id.register_button)
        firstNameText = findViewById(R.id.first_name)
        lastNameText = findViewById(R.id.last_name)
        emailText = findViewById(R.id.editTextEmail)
        passwordText = findViewById(R.id.editTextPassword)
        usersDatabaseRef = FirebaseDatabase.getInstance().getReference("users")


        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener{
            createNewUser()
        }

    }

    private fun createNewUser(){
        val pass = passwordText.text.toString()
        val email = emailText.text.toString()
        val first = firstNameText.text.toString()
        val last = lastNameText.text.toString()
        if (!TextUtils.isEmpty(first) && !TextUtils.isEmpty(last)
            && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {

            mAuth!!
                .createUserWithEmailAndPassword(email!!, pass!!)
                .addOnCompleteListener(this) { task ->
//                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "createUserWithEmail:success")
                        val userId = mAuth!!.currentUser!!.uid
                        //Verify Email
                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("firstName").setValue(first)
                        currentUserDb.child("lastName").setValue(last)
                        usersDatabaseRef.push().setValue(userId)
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@RegisterActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }

    }
}