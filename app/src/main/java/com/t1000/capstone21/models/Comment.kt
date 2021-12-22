package com.t1000.capstone21.models

data class Comment (
    var userId: String = "",
    var commentText: String = "",
    var commentLikes: Long = 0,
)