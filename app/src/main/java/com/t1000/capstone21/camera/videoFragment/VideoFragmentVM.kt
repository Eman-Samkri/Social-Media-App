package com.t1000.capstone21.camera.videoFragment

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class VideoFragmentVM : ViewModel() {

    private val repo = Repo.getInstance()

    fun uploadVideo(localVideoUri: Uri, video: Video) {

        viewModelScope.launch {

            repo.uploadVideoToStorage(localVideoUri,video)

        }
    }


    fun initVideo(timeCreated: Long){

    }
}