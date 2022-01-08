package com.t1000.capstone21.ui.me

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.Photo
import kotlinx.coroutines.launch

class MeViewModel : ViewModel() {

    private val repo = Repo.getInstance()


//    fun uploadPhotoToStorage(localPhotoUri: Uri)  : Uri? {
//var uri:Uri? = null
//        viewModelScope.launch {
//            uri = repo.uploadProfilePhotoToStorage(localPhotoUri)
//        }
//return uri
//    }
}