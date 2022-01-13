package com.t1000.capstone21.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.ChatMessage
import com.t1000.capstone21.models.Comment
import com.t1000.capstone21.models.User
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "CommentViewModel"

class CommentViewModel : ViewModel() {

    private val repo = Repo.getInstance()

    fun saveCommentToFirestore(video: String, comment:Comment){
        viewModelScope.launch {
            repo.saveCommentToFirestore(video,comment)
        }

    }


    fun fetchUserById(userId: String) : LiveData<List<User>> = liveData {
        emit(listOf(repo.fetchUserById(userId)))
    }

//    fun fetchVideosComment(videoId: String) : LiveData<List<Video>> = liveData {
//        emit(repo.fetchVideosById(videoId))
//    }

    suspend fun fetchVideosComment(videoId: String): LiveData<List<Comment>> {
        return  repo.fetchVideosCommentById(videoId)

    }





    fun deleteVideoComment(videoId:String, index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteVideoComment(videoId,index)
            }

    }

}