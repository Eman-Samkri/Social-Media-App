package com.t1000.capstone21.ui.sticker

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.t1000.capstone21.giphy.model.Data
import com.t1000.capstone21.giphy.repo.GiphyRepo

class StickerViewModel : ViewModel() {

    private val repo:GiphyRepo = GiphyRepo()

    val dataLiveData: LiveData<List<Data>> = repo.getStickers()

}