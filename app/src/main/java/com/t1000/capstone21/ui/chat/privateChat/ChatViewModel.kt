package com.t1000.capstone21.ui.chat.privateChat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.ChatMessage
import kotlinx.coroutines.launch

private const val TAG = "ChatViewModel"
class ChatViewModel : ViewModel() {

    private val repo = Repo.getInstance()

    fun sendMessage(message: ChatMessage) {
        repo.sendMessage(message)
    }

    suspend fun loadChatMessages(senderId: String, receiverId: String): LiveData<List<ChatMessage>> {
        return  repo.loadChatMessages(senderId,receiverId)

    }

}














