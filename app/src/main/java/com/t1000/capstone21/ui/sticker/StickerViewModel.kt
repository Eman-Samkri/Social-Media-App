package com.t1000.capstone21.ui.sticker

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t1000.capstone21.Repo
import com.t1000.capstone21.giphy.model.Data
import com.t1000.capstone21.giphy.repo.GiphyRepo
import com.t1000.capstone21.models.Comment
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.launch

class StickerViewModel : ViewModel() {

    private val apiRepo:GiphyRepo = GiphyRepo()

    private val repo = Repo.getInstance()

    val dataLiveData: LiveData<List<Data>> = apiRepo.getStickers()

    fun saveCommentToFirestore(video: Video, comment: Comment){
        viewModelScope.launch {
            repo.saveCommentToFirestore(video,comment)
        }

    }

}