package com.t1000.capstone21.ui.comment

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.Comment
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {

    private val repo = Repo.getInstance()

    fun saveCommentToFirestore(video: Video, comment:Comment){
        viewModelScope.launch {
            repo.saveCommentToFirestore(video,comment)
        }

    }


    fun fetchVideosComment() : LiveData<List<Video>> = liveData {

        emit(repo.fetchVideosComment())


    }
}