package com.t1000.capstone21.camera.baseFragment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t1000.capstone21.models.Photo
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.launch
import java.io.File



class CameraViewModel : ViewModel(){

    private val repo = Repo.getInstance()

    var savedUri : Uri? = null



    fun getPhotoFile(photo: Photo):File = repo.getPhotoFile(photo)

    fun getVideoFile(video: Video):File = repo.getVideoFile(video)

    fun getFileDir() = repo.fileDir


    fun uploadPhotoToStorage(localPhotoUri: Uri, photo: Photo)  {

        viewModelScope.launch {
            repo.uploadPhotoToStorage(localPhotoUri,photo)
        }

    }


    fun uploadVideo(localVideoUri: Uri, video: Video) {
        viewModelScope.launch {
            repo.uploadVideoToStorage(localVideoUri,video)
        }
    }


}