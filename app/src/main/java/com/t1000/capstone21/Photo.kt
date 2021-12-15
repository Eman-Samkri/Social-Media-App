package com.t1000.capstone21

import java.util.*


data class Photo(val photo :String ="") {

    val id = UUID.randomUUID()

    val photoFileName:String
        get() = "IMG$id.jpg"



}