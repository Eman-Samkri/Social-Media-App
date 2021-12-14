package com.t1000.capstone21

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Repo private constructor(context: Context) {



    val fileDir : File = context.applicationContext.filesDir
    fun getPhotoFile(model: Model):File = File(fileDir,model.photoFileName)

    fun getVideoFile(model: Model):File = File(fileDir , model.videoFileName)





    companion object{
        private var INSTANCE:Repo? = null
        fun initialize(context: Context){
            if (INSTANCE == null){
                INSTANCE = Repo(context)
            }
        }

        fun getInstance():Repo = INSTANCE ?: throw IllegalStateException("you must initialize your repo")


    }
}