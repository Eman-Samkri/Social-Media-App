package com.t1000.capstone21

import android.content.Context
import java.io.File

class Repo private constructor(context: Context) {

    private val fileDir = context.applicationContext.filesDir

    fun getPhotoFile(model: Model):File = File(fileDir , model.photoFileName)


    companion object{
       private var INSTANCE:Repo? = null

        fun initialize(context: Context){
        if (INSTANCE == null){
            INSTANCE = Repo(context)
         }
        }

        fun getInstance():Repo = INSTANCE ?: throw IllegalStateException("you must initialize your repo")

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }


    }

}