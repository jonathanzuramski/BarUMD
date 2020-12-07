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

    protected lateinit var sharedPreferences: SharedPreferences
    private lateinit var toolbar: Toolbar
    private var uid: String? = null
    private lateinit var listViewBars : ListView
    private lateinit var bars : MutableList<Bar>
    var favs : MutableList<String> = ArrayList<String>()
    private lateinit var barsDatabase: DatabaseReference
    lateinit var favDatabase: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        barsDatabase = FirebaseDatabase.getInstance().getReference("bars")
        favDatabase =  FirebaseDatabase.getInstance().getReference("users")

        sharedPreferences = getSharedPreferences("accountInfo", Context.MODE_PRIVATE)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        listViewBars = findViewById(R.id.listViewBars)
        bars = ArrayList<Bar>()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            uid = user.uid


        }

        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener{ item ->
            when(item.itemId) {

                R.id.setting -> {startActivity(Intent(this@MainActivity, SettingsActivity::class.java));
                    true}
                /*
                R.id.addBar -> {
                    val bar = Bar("testBar", "123 Abc St", "123-456-7890",
                        5.0,"1700","2330")
                    barsDatabase.child(bar.name!!).setValue(bar)
                    true
                }
                */
                R.id.favorites -> {startActivity(Intent(this@MainActivity, FavoritesActivity::class.java))
                    true}
                else -> true
            }
        }


        // get stored user account
//        uid = sharedPreferences.getString("uid",null)

        // set click listners
        listViewBars.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            //getting the selected artist
            val bar = bars[i]

            //creating an intent
            val intent = Intent(applicationContext, BarDetailActivity::class.java)

            intent.putExtra("barObj",bar)
            startActivity(intent)
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
                    val barAdapter = BarAdapter(this@MainActivity, bars)
                    listViewBars.adapter = barAdapter
                }
            }

        })



    }




}