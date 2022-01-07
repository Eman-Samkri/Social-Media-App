package com.t1000.capstone21.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.ChatMessage

class ChatViewModel : ViewModel() {

    private val repo = Repo.getInstance()

    fun sendMessage(message: ChatMessage) {
        repo.sendMessage(message)
    }

    fun loadChatMessages(senderId: String, receiverId: String): LiveData<List<ChatMessage>> {
        return repo.loadChatMessages(senderId,receiverId)

    }

}