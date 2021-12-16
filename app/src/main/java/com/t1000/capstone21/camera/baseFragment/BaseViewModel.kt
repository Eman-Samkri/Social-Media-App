package com.t1000.capstone21.camera.baseFragment

import androidx.lifecycle.ViewModel
import com.t1000.capstone21.models.Photo
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.Video
import java.io.File

class BaseViewModel : ViewModel(){

    private val repo = Repo.getInstance()


    fun getPhotoFile(model: Photo):File = repo.getPhotoFile(model)

    fun getVideoFile(model: Video):File = repo.getVideoFile(model)

    fun getFileDir() = repo.fileDir


}