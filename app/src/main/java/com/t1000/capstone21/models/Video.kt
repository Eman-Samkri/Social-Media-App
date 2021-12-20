package com.t1000.capstone21.models

import java.util.*

data class Video (var username: String = "",
                  var videoUrl: String = "",
                  var likes: String = "",
                  var comments: List<Comment> = listOf(),
                    ) {

    val videoFileName:String
        get() = "VID${UUID.randomUUID()}.mp4"


}