package com.t1000.capstone21.models

data class ChatParticipant(
    var participant: User,
    var lastMessage: String = ""
)
