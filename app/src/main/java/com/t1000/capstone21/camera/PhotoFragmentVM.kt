package com.t1000.capstone21.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.Photo
import kotlinx.coroutines.launch

class PhotoFragmentVM:ViewModel() {

    private val repo = Repo.getInstance()

    fun uploadPhotoToStorage(localPhotoUri: Uri,photo: Photo)  {

        viewModelScope.launch {

            repo.uploadPhotoToStorage(localPhotoUri,photo)

        }



    }

}