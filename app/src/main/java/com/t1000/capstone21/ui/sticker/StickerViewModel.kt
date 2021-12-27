package com.t1000.capstone21.ui.sticker

import androidx.annotation.NonNull
import androidx.lifecycle.*
import com.t1000.capstone21.Repo
import com.t1000.capstone21.giphy.model.Data
import com.t1000.capstone21.giphy.repo.GiphyRepo
import com.t1000.capstone21.models.Comment
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StickerViewModel : ViewModel() {

    private val apiRepo:GiphyRepo = GiphyRepo()

    private val repo = Repo.getInstance()

    private val searchTermLiveData:MutableLiveData<String> = MutableLiveData()


   // val dataLiveData: LiveData<List<Data>> = apiRepo.getStickers()

    fun stickersLiveData():LiveData<List<Data>> {
        var tempList:List<Data> = emptyList()
        val tempLiveData: MutableLiveData<List<Data>> = MutableLiveData()



        return Transformations.switchMap(searchTermLiveData) { term->
            viewModelScope.launch(Dispatchers.IO) {

                tempList = if (term.isBlank()){
                    apiRepo.getStickers()
                }else{
                    apiRepo.searchStickers(term)
                }
            }.invokeOnCompletion {

                viewModelScope.launch {
                    tempLiveData.value = tempList
                }
            }

            tempLiveData
        }
    }


    fun saveCommentToFirestore(video: Video, comment: Comment){
        viewModelScope.launch {
            repo.saveCommentToFirestore(video,comment)
        }

    }

}