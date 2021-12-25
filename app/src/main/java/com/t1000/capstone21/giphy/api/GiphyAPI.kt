package com.t1000.capstone21.giphy.api

import com.t1000.capstone21.giphy.models.StickerResponse
import retrofit2.Response
import retrofit2.http.GET

interface GiphyAPI {

    @GET("/v1/gifs/trending?" +
            "api_key=AcOnDAIHYdczRNn5ZRhvCGqQy7dYRQti&" +
            "limit=&" +
            "rating=pg/")
    suspend fun getStickers(): Response<StickerResponse>

}