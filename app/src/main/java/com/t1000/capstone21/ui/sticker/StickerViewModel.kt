package com.t1000.capstone21.ui.sticker

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.t1000.capstone21.giphy.models.Sticker
import com.t1000.capstone21.giphy.repo.StipopRepo

class StickerViewModel : ViewModel() {

    private val repo:StipopRepo = StipopRepo()

    val dataLiveData: LiveData<List<Sticker>> = repo.getStickers()

}