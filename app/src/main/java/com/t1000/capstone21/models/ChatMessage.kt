package com.t1000.capstone21.models

import com.google.firebase.Timestamp
import java.util.*

data class ChatMessage (val senderId :String,
                        val receiverId :String,
                        val text: String = "",
                        var type: String = "",
                        val created_at: Timestamp?
                        )
