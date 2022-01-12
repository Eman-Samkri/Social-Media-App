package com.t1000.capstone21.ui.searchUser

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.User
import com.t1000.capstone21.models.Video

class SearchUserViewModel : ViewModel() {

    private val repo = Repo.getInstance()


    fun  fetchAllUser(): LiveData<List<User>> = liveData {
        emit(repo.fetchAllUser())
    }
}