package com.t1000.capstone21.camera.baseFragment

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import com.t1000.capstone21.Model
import com.t1000.capstone21.Repo
import java.io.File

class BaseViewModel : ViewModel(){

    private val repo = Repo.getInstance()

//    fun createFile(baseFolder: File, format: String, extension: String)
//    = repo.createFile(baseFolder,format,extension)

    fun getPhotoFile(model: Model):File = repo.getPhotoFile(model)

    fun getVideoFile(model: Model):File = repo.getVideoFile(model)

    fun getFileDir() = repo.fileDir


}