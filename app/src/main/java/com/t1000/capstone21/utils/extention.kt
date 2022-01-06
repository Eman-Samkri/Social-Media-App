package com.t1000.capstone21.utils

fun String.formatSeconds(seconds:Int):String?{
    return (getTowDecimalsValue(seconds/3600)+":" +
            getTowDecimalsValue(seconds/60)+":" +
            getTowDecimalsValue(seconds%60))
}


private fun getTowDecimalsValue(value :Int):String{
    return if(value in 0..9) {"0$value"}
    else {value.toString()}
}