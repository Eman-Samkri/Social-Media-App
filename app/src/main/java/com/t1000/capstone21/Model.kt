package com.t1000.capstone21

data class Model(var id:Int = 0) {

    val photoFileName:String
        get() = "IMG$id.jpg"


    val videoFileName:String
        get() = "VID$id.mp4"


}