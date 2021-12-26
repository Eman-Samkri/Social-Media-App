package com.t1000.capstone21.giphy.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.t1000.capstone21.giphy.api.GiphyAPI
import com.t1000.capstone21.giphy.model.Data
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "StipopRepo"
open class GiphyRepo {



    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.giphy.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val stipopApi:GiphyAPI = retrofit.create(GiphyAPI::class.java)




    fun getStickers(): LiveData<List<Data>> {
        return liveData(Dispatchers.IO) {
            val response = stipopApi.getStickers()
            if (response.isSuccessful){
                response.body()?.data?.let {
                    emit(it)
                }

            }else{
                Log.e(TAG , "the error is ${response.errorBody()}")
            }
        }

    }



}