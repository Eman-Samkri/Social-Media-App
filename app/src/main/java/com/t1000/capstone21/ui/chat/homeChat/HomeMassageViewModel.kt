package com.t1000.capstone21.ui.chat.homeChat

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.ChatParticipant


class HomeMassageViewModel : ViewModel() {

    private val repo = Repo.getInstance()

     fun getChatParticepent(): LiveData<List<ChatParticipant?>> = liveData {
        emit(listOf(repo.getChatParticepent()))
    }





}