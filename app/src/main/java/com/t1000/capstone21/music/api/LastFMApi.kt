package com.t1000.capstone21.music.api

import com.t1000.capstone21.music.model.LastFM
import retrofit2.Response
import retrofit2.http.GET

interface LastFMApi {

    @GET("/2.0/?method=chart.gettoptracks&" +
            "api_key=2d987d9413b169ef4d17e60e1f9196d4&" +
            "format=json")
    suspend fun getMusic(): Response<LastFM>

}