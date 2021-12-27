package com.t1000.capstone21.giphy.api

import com.t1000.capstone21.giphy.model.Sticker
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyAPI {

    @GET("/v1/gifs/trending?")
    suspend fun getStickers(): Call<Sticker>


    @GET("v1/gifs/search?")
    fun searchStickers(@Query("text") query: String): Call<Sticker>

}