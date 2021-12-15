package com.t1000.capstone21

import android.content.Context
import com.t1000.capstone21.models.Photo
import com.t1000.capstone21.models.Video
import java.io.File

class Repo private constructor(context: Context) {



    val fileDir : File = context.applicationContext.filesDir
    fun getPhotoFile(model: Photo):File = File(fileDir,model.photoFileName)

    fun getVideoFile(model: Video):File = File(fileDir , model.videoFileName)

    //fun uploadVidToStorage(fileName : String) =




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