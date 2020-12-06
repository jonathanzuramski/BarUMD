package com.example.barze

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*

class BarDetailActivity : Activity() {

    private lateinit var bar : Bar
    private lateinit var reviewListView : ListView
    private lateinit var reviews : MutableList<Review>
    private lateinit var waitTimeTextView : TextView
    private lateinit var waitInfoDatabaseRef : DatabaseReference
    private lateinit var reviewDatabaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        bar = intent.getSerializableExtra("barObj") as Bar

        val nameTextView = findViewById<TextView>(R.id.detail_name)
        val addressTextView = findViewById<TextView>(R.id.detail_address)
        val phoneTextView = findViewById<TextView>(R.id.detail_phone)
        val feeTextView = findViewById<TextView>(R.id.detail_fee)
        val hoursTextView = findViewById<TextView>(R.id.detail_hours)
        val statusTextView = findViewById<TextView>(R.id.detail_status)
        val reportWaitBtn = findViewById<Button>(R.id.report_wait_btn)
        val reviewBtn = findViewById<Button>(R.id.add_review_btn)
        val imageView = findViewById<ImageView>(R.id.detail_pic)
        reviewListView = findViewById(R.id.review_list)
        waitTimeTextView = findViewById(R.id.detail_wait_time)

        waitInfoDatabaseRef = FirebaseDatabase.getInstance().getReference("waitInfo").child(bar.name!!)
        reviewDatabaseRef = FirebaseDatabase.getInstance().getReference("reviews").child(bar.name!!)

        reviews = ArrayList()

        nameTextView.text = bar.name
        addressTextView.text = bar.address
        phoneTextView.text = bar.phone
        feeTextView.text = "$${bar.fee}"
        hoursTextView.text = "Opens ${getTimeString(bar.open!!)} - ${getTimeString(bar.close!!)}"
        statusTextView.text = if (bar.isBarOpen()) "OPENING" else "CLOSED"
        reportWaitBtn.setOnClickListener{
            reportWaitTime()
        }
        reviewBtn.setOnClickListener{
            addReview()
        }


        // Get image
        // val imageStorageRef = FirebaseStorage.getInstance().getReference("images")
        //    .child(bar.name!!).child("banner_img.jpg")
        // Glide.with(applicationContext).load(imageStorageRef).into(imageView)



    }

    override fun onStart() {
        super.onStart()

        // get reviews
        reviewDatabaseRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                reviews.clear()
                var review : Review? = null
                for(postSnapshot in snapshot.children){
                    try {
                        review = postSnapshot.getValue(Review::class.java)
                        reviews.add(review!!)
                    } catch (e: Exception) {
                        Log.e("TAG", e.toString())
                    }
                    val reviewAdapter = ReviewAdapter(this@BarDetailActivity, reviews)
                    reviewListView.adapter = reviewAdapter
                }

            }

        })

        // get wait time
        waitInfoDatabaseRef.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val waitInfo = snapshot.getValue(WaitInfo::class.java)
                bar.waitInfo = waitInfo

                // update text views
                if (waitInfo != null && System.currentTimeMillis() - waitInfo.reportTime < ONE_HOUR) {
                    // only valid if last report is within 1 hour
                    waitTimeTextView.text = "${waitInfo.waitTime} min"
                } else {
                    waitTimeTextView.text = "No Info"
                }
            }

        })
    }

    fun getTimeString(fourDigit:String) : String {
        var hour = fourDigit.substring(0,2).toInt()
        val min = fourDigit.substring(2).toInt()
        val am = hour < 12
        if (!am){
            hour -= 12
        }
        if (hour == 0){
            hour = 12
        }
        return "$hour:$min ${if(am) "AM" else "PM"}"
    }

    private fun reportWaitTime(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.report_wait_layout, null)
        dialogBuilder.setView(dialogView)

        val editTextTime = dialogView.findViewById(R.id.report_dialog_text) as EditText
        val buttonSubmit = dialogView.findViewById(R.id.wait_time_submit_btn) as Button

        dialogBuilder.setTitle("Report Wait Time")
        val b = dialogBuilder.create()
        b.show()

        buttonSubmit.setOnClickListener{
            val waitTime = editTextTime.text.toString().toInt()
            val waitInfo = WaitInfo(waitTime)
            waitInfoDatabaseRef.setValue(waitInfo)
            Toast.makeText(this,"Reported",Toast.LENGTH_LONG).show()
        }
    }

    private fun addReview(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.add_review_dialog, null)
        dialogBuilder.setView(dialogView)

        val editTextNickname = dialogView.findViewById<EditText>(R.id.review_nickname)
        val scoreSeekBar = dialogView.findViewById<SeekBar>(R.id.score_bar)
        val editTextComment = dialogView.findViewById<EditText>(R.id.review_dialog_comment)
        val textViewScore = dialogView.findViewById<TextView>(R.id.review_dialog_score)
        val buttonSubmit = dialogView.findViewById<Button>(R.id.review_submit_btn)
        var score = 5
        textViewScore.text = "Your score: $score"

        scoreSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                score = progress
                textViewScore.text = "Your score: $score"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        dialogBuilder.setTitle("Add Review")
        val b = dialogBuilder.create()
        b.show()

        buttonSubmit.setOnClickListener{
            val nickname = editTextNickname.text.toString()
            val comment = editTextComment.text.toString()
            val review = if (comment.isNullOrBlank()) Review(nickname,score)
                            else Review(nickname,score,comment)
            val key = reviewDatabaseRef.push().key
            reviewDatabaseRef.child(key!!).setValue(review)
            Toast.makeText(this,"Reported",Toast.LENGTH_LONG).show()
        }
    }

    companion object{
        val ONE_HOUR : Long = 1000*3600
    }
}