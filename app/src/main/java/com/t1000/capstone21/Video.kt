package com.t1000.capstone21

import java.util.*

data class Video (val video :String ="") {

    val id = UUID.randomUUID()

    val videoFileName:String
        get() = "VID$id.mp4"
}