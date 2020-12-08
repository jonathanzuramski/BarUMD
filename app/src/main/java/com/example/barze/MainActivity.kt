package com.example.barze

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.database.*
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var uid: String
    private lateinit var listViewBars : ListView
    private lateinit var bars : ArrayList<Bar>
    private lateinit var barsDatabase: DatabaseReference
    private lateinit var userFavoritesRef : DatabaseReference
    private lateinit var userFavoriteList : ArrayList<String>
    private var favoriteOn = false
    private var favInited = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            uid = user.uid


        }

//        uid = intent.getStringExtra("uid")!!
        barsDatabase = FirebaseDatabase.getInstance().getReference("bars")
        userFavoritesRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        toolbar = findViewById<Toolbar>(R.id.toolbar)
        listViewBars = findViewById(R.id.listViewBars)

        bars = ArrayList()
        userFavoriteList = ArrayList()

        // get stored user account


        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener{ item ->
            when(item.itemId) {

                R.id.setting -> {startActivity(Intent(this@MainActivity, SettingsActivity::class.java));
                    true}

                R.id.favorites -> {

                    // if currently shows all bars
                    if (!favoriteOn){
                        val barAdapter = BarAdapter(this@MainActivity, favFilter(bars))
                        listViewBars.adapter = barAdapter
                        favoriteOn = true

                        // change text of menu item
                        item.title = "Show All"
                    } else {
                        // When currently show favorite only

                        val barAdapter = BarAdapter(this@MainActivity, bars)
                        listViewBars.adapter = barAdapter
                        favoriteOn = false

                        // change text of menu item
                        item.title = "Show Favorite Only"
                    }

                    true
                }

                else -> true
            }




        }




        // set click listners
        listViewBars.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            //getting the selected artist
            val bar = bars[i]

            //creating an intent
            val intent = Intent(applicationContext, BarDetailActivity::class.java)

            intent.putExtra("barObj",bar)
            startActivity(intent)
        }

        // long click to add favorite
        listViewBars.onItemLongClickListener = AdapterView.OnItemLongClickListener{adapterView, view, i, l ->
            val bar = bars[i]
            if (userFavoriteList.contains(bar.name!!)){
                // cancel favorite
                userFavoriteList.remove(bar.name!!)

                // update to database
                userFavoritesRef.setValue(userFavoriteList)



            } else {
                // add fav to local
                userFavoriteList.add(bar.name!!)

                // add fav to database
                userFavoritesRef.setValue(userFavoriteList)


            }
            true
        }

    }

    override fun onStart(){
        super.onStart()

        barsDatabase.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("TAG","Entered onDataChange()")
                bars.clear()

                var bar : Bar? = null
                for(postSnapshot in snapshot.children){
                    try {
                        bar = postSnapshot.getValue(Bar::class.java)
                        bars.add(bar!!)
                    } catch (e: Exception) {
                        Log.e("TAG", e.toString())
                    }
                    val barAdapter = BarAdapter(this@MainActivity, if (favoriteOn) favFilter(bars) else bars)
                    listViewBars.adapter = barAdapter
                }
            }

        })

        // get user favorite list
        userFavoritesRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for(data in snapshot.children) {
                    userFavoriteList.add(data.getValue().toString())
                }
            }
        })



    }

    fun favFilter(bars:ArrayList<Bar>):ArrayList<Bar> {
        val filtered = ArrayList<Bar>()
        for (bar in bars){
            if (userFavoriteList!!.contains(bar.name)) {
                filtered.add(bar)
            }
        }
        return filtered
    }


}
