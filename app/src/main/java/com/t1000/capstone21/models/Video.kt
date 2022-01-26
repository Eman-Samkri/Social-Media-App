package com.t1000.capstone21.models

import java.util.*

data class Video (
                  val videoId:String = UUID.randomUUID().toString(),
                  var userId: String = "",
                  var videoUrl: String = "",
                  var likes: List<String> = listOf(),
                  var comments: List<Comment> = listOf(),
                    ) {


    val videoFileName:String
        get() = "VID${UUID.randomUUID()}.mp4"


}