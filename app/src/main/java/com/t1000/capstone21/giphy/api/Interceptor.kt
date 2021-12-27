package com.t1000.capstone21.giphy.api

import okhttp3.Interceptor
import okhttp3.Response


private const val API_KEY = "AcOnDAIHYdczRNn5ZRhvCGqQy7dYRQti"
class Interceptor : Interceptor{



    override fun intercept(chain: Interceptor.Chain): Response {

        val currentRequest = chain.request()


        val newUrl = currentRequest.url().newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("limit","25")
            .addQueryParameter("rating","pg")
            .addQueryParameter("lang","en")
            .addQueryParameter("offset","0")
            .build()


        val newRequest = currentRequest.newBuilder()
            .url(newUrl).build()

        return chain.proceed(newRequest)
    }
}