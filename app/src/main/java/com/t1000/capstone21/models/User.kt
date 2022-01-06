package com.t1000.capstone21.models

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


data class User(
    val userId: String = Firebase.auth.currentUser!!.toString(),
    val username: String = "",
    var profilePictureUrl: String? = null,
    var followers: List<String> = listOf(),
    var following: List<String> = listOf(),
   // val token :String = ""
) {

}