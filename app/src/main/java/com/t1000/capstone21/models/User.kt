package com.t1000.capstone21.models

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


data class User(
    val userId: String = "",
    val username: String = "",
    var profilePictureUrl: String? = "",
    var followers: List<String> = listOf(),
    var following: List<String> = listOf()
) {

}