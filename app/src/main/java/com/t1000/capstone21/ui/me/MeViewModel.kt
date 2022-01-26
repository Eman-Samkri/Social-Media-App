package com.t1000.capstone21.ui.me

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.Photo
import kotlinx.coroutines.launch

class MeViewModel : ViewModel() {

    private val repo = Repo.getInstance()


    fun uploadProfilePhoto(localPhotoUri: Uri) {
        viewModelScope.launch {
            repo.uploadProfilePhoto(localPhotoUri)
        }

    }

     fun savePhotoUrlToFirestore(uri:Uri){
        viewModelScope.launch {
            repo.savePhotoUrlToFirestore(uri)
        }
    }
}