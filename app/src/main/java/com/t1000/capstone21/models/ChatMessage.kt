package com.t1000.capstone21.models

import com.google.firebase.Timestamp
import java.util.*

data class ChatMessage (val text: String = "",
                        var type: String = "",
                        val time : Date = Date(),
                        val created_at: Timestamp?)
