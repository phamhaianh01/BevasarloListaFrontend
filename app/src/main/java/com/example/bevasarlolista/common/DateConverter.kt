package com.example.bevasarlolista.common

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateConverter {

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    fun convertToSimpleDate(date: Date?): String{
        if(date == null)
            return ""
        val format = SimpleDateFormat("yyyy.MM.dd")
        return format.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    fun convertStringToDate(date: String): Date {
        val format = SimpleDateFormat("yyyy.MM.dd")
        return format.parse(date)
    }
}