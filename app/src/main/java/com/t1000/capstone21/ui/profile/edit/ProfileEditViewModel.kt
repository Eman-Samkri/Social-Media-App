package com.t1000.capstone21.ui.profile.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t1000.capstone21.Repo
import com.t1000.capstone21.models.Photo
import kotlinx.coroutines.launch
import java.io.File

class ProfileEditViewModel : ViewModel() {

    private val repo = Repo.getInstance()

    fun uploadProfilePhoto(localPhotoUri: Uri) {
        viewModelScope.launch {
            repo.uploadProfilePhoto(localPhotoUri)
        }
    }

}