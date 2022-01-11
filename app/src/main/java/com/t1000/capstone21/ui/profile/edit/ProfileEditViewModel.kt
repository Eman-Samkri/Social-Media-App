package com.t1000.capstone21.ui.profile.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t1000.capstone21.Repo
import kotlinx.coroutines.launch

class ProfileEditViewModel : ViewModel() {

    private val repo = Repo.getInstance()

    fun uploadProfilePhoto(localPhotoUri: Uri) {
        viewModelScope.launch {
            repo.uploadProfilePhoto(localPhotoUri)
        }

    }
}