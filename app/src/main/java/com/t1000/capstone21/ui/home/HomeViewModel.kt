package com.t1000.capstone21.ui.home

import androidx.lifecycle.*
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.User
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repo = Repo.getInstance()
    fun fetchRandomVideos() : LiveData<List<Video>> = liveData {
            emit(repo.fetchRandomVideos())
    }

    fun addLike(video:Video){
        repo.addLike(video)
    }

    fun fetchUserById(userId: String) : LiveData<List<User>> = liveData {
        emit(listOf(repo.fetchUserById(userId)))
    }






//     fun fetchRandomVideos() : List<Video> {
//       viewModelScope.launch {
//           repo.fetchRandomVideos()
//       }
//
//     }






}