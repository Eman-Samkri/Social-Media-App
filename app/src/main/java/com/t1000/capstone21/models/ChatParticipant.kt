package com.t1000.capstone21.models

data class ChatParticipant (var participantId: String = "",
                            var lastMessage: String = "",
                            var lastMessageDate: Map<String, Double>? = null,
                            var messageType: String? = null)