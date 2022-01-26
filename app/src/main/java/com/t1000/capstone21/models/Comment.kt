package com.t1000.capstone21.models

data class Comment(
    var userId: String = "",
    var videoId: String = "",
    var commentText: String = "",
    var commentLikes: Long = 0,
    var commentType:String =""
)