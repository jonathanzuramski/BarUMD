package com.example.barze

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ReviewAdapter (private val context: Activity, private var reviews: List<Review>) : ArrayAdapter<Review>(context,
    R.layout.review_item, reviews) {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.review_item, null, true)

        val textViewUser = listViewItem.findViewById<TextView>(R.id.review_user)
        val textViewScore = listViewItem.findViewById<TextView>(R.id.review_score)
        val textViewComment = listViewItem.findViewById<TextView>(R.id.review_comment)

        val review = reviews[position]
        textViewUser.text = review.user
        textViewScore.text = "Rating: ${review.score}"
        textViewComment.text = review.comment
        return listViewItem
    }
}