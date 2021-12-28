package com.t1000.capstone21.giphy.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.t1000.capstone21.giphy.api.GiphyAPI
import com.t1000.capstone21.giphy.api.Interceptor
import com.t1000.capstone21.giphy.model.Data
import com.t1000.capstone21.giphy.model.Sticker
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "GiphyRepo"

open class GiphyRepo {


    private  val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor())
        .build()


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.giphy.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val giphyApi:GiphyAPI = retrofit.create(GiphyAPI::class.java)




    fun getStickers(): LiveData<List<Data>> {
        return liveData(Dispatchers.IO) {
            val response = giphyApi.getStickers()
            if (response.isSuccessful){
                response.body()?.data?.let {
                    emit(it)
                }

            }else{
                Log.e(TAG , "the error is ${response.errorBody()}")
            }
        }

    }
//
//    fun searchStickers(query: String): LiveData<List<Data>> {
//        return liveData(Dispatchers.IO) {
//            val response = giphyApi.searchStickers(query)
//            if (response.isSuccessful){
//                response.body()?.data?.let {
//                    emit(it)
//                }
//
//            }else{
//                Log.e(TAG , "the error is ${response.errorBody()}")
//            }
//        }
//
//    }

//    suspend fun getStickers():List<Data> = fetchStickerMetaData(giphyApi.getStickers())


//    suspend fun searchStickers(query: String): List<Data> {
//        return fetchStickerMetaData(giphyApi.searchStickers(query))
//    }

//
//    private  fun fetchStickerMetaData(response: Response<Sticker>) : List<Data> {
//
//        if (response.isSuccessful){
////            stickerItems = response.body()?.data ?: emptyList()
////            stickerItems = stickerItems.filter { it.images.downsized_small.mp4.isBlank() }
//            response.body()?.data?.let {
//
//                    emit(it)
//            }
//
//
//        }else{
//            Log.e(TAG , "something gone wrong ${response.errorBody()}")
//        }
//
//        return stickerItems
//
//    }



}