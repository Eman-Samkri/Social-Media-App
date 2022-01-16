package com.t1000.capstone21.music.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.t1000.capstone21.music.api.LastFMApi
import com.t1000.capstone21.music.model.Track
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "LastFMRepo"
open class LastFMRepo {



    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://ws.audioscrobbler.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val lastFMApi:LastFMApi= retrofit.create(LastFMApi::class.java)




     fun getMusic(): LiveData<List<Track>>{
         return liveData(Dispatchers.IO) {
             val response = lastFMApi.getMusic()
             if (response.isSuccessful) {
                 Log.e(TAG, "getMusic: first $response",)
                 response.body()?.tracks?.track?.let {
                     emit(it)
                 }

             } else {
                 Log.e(TAG, "the error is ${response.errorBody()}")
             }
         }
    }




}