package com.t1000.capstone21.models


data class User(
    val userId: String,
    val username: String = "",
    val email: String = "",
    val imagesUrl : List<Photo> = listOf(),
    val videosUrl : List<Video> = listOf()
)