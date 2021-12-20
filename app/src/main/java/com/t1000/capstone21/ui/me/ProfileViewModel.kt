package com.t1000.capstone21.ui.me

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.User
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val userRepo = Repo.getInstance()



    fun fetchUser() {
        viewModelScope.launch {
             userRepo.fetchUserVideo()
        }
    }


}