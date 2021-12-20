package com.t1000.capstone21.models

import java.util.*


data class Photo(var username: String = "",
                 var photoUrl: String = "",
                 var likes: String = "",
                 var comments: String = "") {

    val photoFileName:String
        get() = "IMG${UUID.randomUUID()}.jpg"



}