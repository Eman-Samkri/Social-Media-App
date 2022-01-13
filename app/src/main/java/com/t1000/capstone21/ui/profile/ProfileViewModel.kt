package com.t1000.capstone21.ui.profile

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
        emit(listOf(repo.fetchUserById(userId)))
    }


    fun fetchVideosUser(userId: String) : LiveData<List<Video>> = liveData {
        emit(repo.fetchVideosUser(userId))
    }

    fun addFollowing(userId :String){
        viewModelScope.launch {
            repo.addFollowing(userId)
        }
    }

    fun sendNotificationToUser(userId :String, title:String ,message:String){
        viewModelScope.launch {
            repo.sendNotificationToUser(userId,title,message)
        }
    }

    fun removeUserToken() {
        repo.removeUserToken()
    }

    fun unFollow(userId :String){
        viewModelScope.launch {
            repo.deleteUserFollow(userId)
        }
    }

    fun deleteVideo(videoId:String){
        viewModelScope.launch {
            repo.deleteVideo(videoId)
        }
    }



}