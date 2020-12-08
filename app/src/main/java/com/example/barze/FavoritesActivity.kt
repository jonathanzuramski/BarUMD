package com.example.barze

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class FavoritesActivity  : AppCompatActivity(){
    private lateinit var userFavorites : DatabaseReference
    private lateinit var bars : MutableList<Bar>
    private lateinit var listViewBars : ListView
    private lateinit var barsDatabase: DatabaseReference
    private lateinit var toolbar : Toolbar
    private lateinit var favs : MutableList<String>
    private lateinit var uid : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        userFavorites = FirebaseDatabase.getInstance().getReference("users")
        barsDatabase = FirebaseDatabase.getInstance().getReference()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            uid = user.uid


        }


//        sharedPreferences = getSharedPreferences("accountInfo", Context.MODE_PRIVATE)
        toolbar = findViewById<Toolbar>(R.id.toolbar1)
        listViewBars = findViewById(R.id.favorites_list)
        bars = ArrayList<Bar>()
        favs = ArrayList<String>()

//        uid = sharedPreferences.getString("uid",null)

        // set click listners
        listViewBars.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            //getting the selected artist
            val bar = bars[i]

            //creating an intent
            val intent = Intent(applicationContext, BarDetailActivity::class.java)

            intent.putExtra("barObj", bar)
            startActivity(intent)
        }


    }



    override fun onStart(){
        super.onStart()




        barsDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("TAG", "Entered onDataChange()")
                bars.clear()

                var bar: Bar? = null

                val map = snapshot.child("users").child(uid).value as HashMap<*, *>
                for (postSnapshot in snapshot.child("bars").children) {
                    try {

                        bar = postSnapshot.getValue(Bar::class.java)

                        for(i in map.values) {
                            Log.i("1st", i.toString())
                            Log.i("NOOO", bar!!.name!!)
                            if(bar!!.name!!.equals(i)) {
                                Log.i("Inside", i.toString())
                                bars.add(bar!!)
                            }
                        }


                    } catch (e: Exception) {
                        Log.e("TAG", e.toString())
                    }
                    val barAdapter = BarAdapter(this@FavoritesActivity, bars)
                    listViewBars.adapter = barAdapter
                }
            }

        })










    }

}