package com.t1000.capstone21.ui.me

import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.User
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"
class ProfileViewModel : ViewModel() {

    private val repo = Repo.getInstance()

    fun fetchUserById(userId: String) : LiveData<List<User>> = liveData {
        emit(repo.fetchUserById(userId))
    }

//    fun fetchUser(): LiveData<User> = liveData {
//        emit(repo.fetchUser())
//        Log.e(TAG, "fetchUser: ${repo.fetchUser()}", )
//    }


}