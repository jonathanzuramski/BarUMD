package com.example.barze

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.renderscript.Sampler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


class BarAdapter(private val context: Activity, private var bars: List<Bar>) : ArrayAdapter<Bar>(context,
    R.layout.bar_item, bars) {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.bar_item, null, true)

        val textViewName = listViewItem.findViewById<View>(R.id.bar_name) as TextView
        val textViewAddress = listViewItem.findViewById<View>(R.id.bar_address) as TextView
        val textViewRating = listViewItem.findViewById<View>(R.id.bar_rating) as TextView
        val textViewStatus = listViewItem.findViewById<View>(R.id.bar_status) as TextView
        val imageView = listViewItem.findViewById<View>(R.id.bar_demo_pic) as ImageView

        val bar = bars[position]
        //val imageStorageRef = FirebaseStorage.getInstance().getReference("images")
        //    .child(bar.name!!).child("banner_img.jpg")
        textViewName.text = bar.name
        textViewAddress.text = bar.address
        textViewRating.text = bar.getRating()
        val isOpen = bar.isBarOpen()
        textViewStatus.text = if (isOpen) "OPEN" else "CLOSED"
        textViewStatus.setTextColor(if (isOpen) Color.GREEN else Color.RED)
        //Glide.with(context).load(imageStorageRef).into(imageView)
        return listViewItem
    }
}