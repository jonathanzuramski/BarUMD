package com.example.barze

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import java.util.jar.Manifest

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val logoutButton =findViewById<Button>(R.id.logout)
        val addPhotoButton = findViewById<Button>(R.id.addphoto)

        logoutButton.setOnClickListener{
            val  mAuth = FirebaseAuth.getInstance()
            mAuth.signOut()
            val intent = Intent(this@SettingsActivity,LoginActivity::class.java)
            startActivity(intent)
        }

        addPhotoButton.setOnClickListener {

        }


    }


    private fun imageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMG_CODE)
    }


    companion object {
        private val IMG_CODE = 1000;
    }

}