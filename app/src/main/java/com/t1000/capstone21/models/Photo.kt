package com.t1000.capstone21.models

import java.util.*


data class Photo(val photo :String ="") {

    val id = UUID.randomUUID()

    val photoFileName:String
        get() = "IMG$id.jpg"



}