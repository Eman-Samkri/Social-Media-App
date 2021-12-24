package com.t1000.capstone21.stipop.api

import com.t1000.capstone21.stipop.models.StickerResponse
import retrofit2.Response
import retrofit2.http.GET

interface StipopAPI {

    @GET("/v1/gifs/trending?" +
            "api_key=AcOnDAIHYdczRNn5ZRhvCGqQy7dYRQti&" +
            "limit=&" +
            "rating=pg/")
    suspend fun getStickers(): Response<StickerResponse>

}