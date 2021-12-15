package com.t1000.capstone21

import java.util.*


data class Model(var id: UUID = UUID.randomUUID()) {


    val photoFileName:String
        get() = "IMG$id.jpg"


    val videoFileName:String
        get() = "VID$id.mp4"


}