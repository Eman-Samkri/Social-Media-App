package com.t1000.capstone21.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun formatSeconds(seconds: Int):String?{
    return (getTowDecimalsValue(seconds/3600)+":" +
            getTowDecimalsValue(seconds/60)+":" +
            getTowDecimalsValue(seconds%60))
}


private fun getTowDecimalsValue(value :Int):String{
    return if(value in 0..9) {"0$value"}
    else {value.toString()}
}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}