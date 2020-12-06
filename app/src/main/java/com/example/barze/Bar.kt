package com.example.barze

import com.google.firebase.database.Exclude
import java.text.SimpleDateFormat
import java.util.*
import java.io.Serializable

class Bar : Serializable {
    var name: String? = null
    var address: String? = null
    var phone: String? = null
    var fee: Double? = null
    var open: String? = null // 4 digit representing time in 24 hour
    var close: String? = null
    private var totalRates : Int = 0
    private var totalScore : Int = 0
    @Exclude
    var waitInfo : WaitInfo? = null

    constructor()

    constructor(name:String, address:String, phone:String, fee:Double, open:String, close:String){
        this.name = name
        this.address = address
        this.phone = phone
        this.fee = fee
        this.open = open
        this.close = close
    }

    @Exclude
    fun isBarOpen():Boolean{
        val dateFormatH = SimpleDateFormat("H")
        val dateFormatm = SimpleDateFormat("m")
        val currLocTime = Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).time
        val timeH = dateFormatH.format(currLocTime)
        val timeMin = dateFormatm.format(currLocTime)
        var closeTime : Int? = null
        if (close!! <= open!!){
            closeTime = close!!.toInt() + 2400
        }
        return closeTime!! - (timeH+timeMin).toInt() > 0
    }

    @Exclude
    fun getRating():String{
        if (totalRates == 0){
            return "No Rating"
        }
        val rating:Double = totalScore.toDouble() / totalRates
        return "%.2f".format(rating)
    }
}