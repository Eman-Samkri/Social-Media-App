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

    fun saveCommentToFirestore(comment: Comment, commentText:String){
        viewModelScope.launch {
            repo.saveCommentToFirestore(comment,commentText)
        }

    }


    fun fetchVideosComment() : LiveData<List<Comment>> = liveData {

        emit(repo.fetchVideosComment())


    }
}