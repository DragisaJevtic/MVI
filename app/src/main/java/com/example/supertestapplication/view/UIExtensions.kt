package com.example.supertestapplication.view

import android.icu.text.SimpleDateFormat
import android.view.View

fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun String?.prepareDate(): String {
    if(this.isNullOrEmpty()) return "No date provided"
    val inFormat = SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'")
    val outFormat = SimpleDateFormat("dd MMM yyyy 'at' HH:mm:ss")
    val date = inFormat.parse(this)
    return outFormat.format(date)
}

fun String?.checkProvidedDate(): String {
    if(this.isNullOrEmpty()) return "No date provided"
    return this
}