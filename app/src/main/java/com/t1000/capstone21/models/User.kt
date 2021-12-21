package com.t1000.capstone21.models


data class User(
    val userId: String,
    val username: String = "",
    val email: String = "",
    var profilePictureUrl: String?,
    var followers: Int = 0,
    var following: Int = 0
)