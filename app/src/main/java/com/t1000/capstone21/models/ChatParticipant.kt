package com.t1000.capstone21.models

data class ChatParticipant(

//    val senderId :String,
//    val receiverId :String
    var participant: User,
    var lastMessage: String = ""
)
