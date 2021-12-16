package com.t1000.capstone21

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.t1000.capstone21.models.Photo
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class Repo private constructor(context: Context) {



    val fileDir : File = context.applicationContext.filesDir

    private val fireStorage = Firebase.storage.reference


//     @RequiresApi(Build.VERSION_CODES.Q)
//     suspend fun initVideo(timeCreated: Long) =
//        withContext(Dispatchers.IO) {
//
//            // Find all video files on the primary external storage device.
//            val videoCollection =
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    MediaStore.Video.Media.getContentUri(
//                        MediaStore.VOLUME_EXTERNAL_PRIMARY
//                    )
//                } else {
//                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                }
//
//            val videoDetails = ContentValues().apply {
//                put(MediaStore.Video.Media.DISPLAY_NAME, "$timeCreated.mp4")
//                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
//                put(MediaStore.Video.Media.DATE_ADDED, timeCreated)
//                put(MediaStore.Video.Media.RELATIVE_PATH, fileDir.path)
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//                    put(MediaStore.Audio.Media.IS_PENDING, 1)
//            }
//
//            val contentUri =
//                resolver.insert(videoCollection, videoDetails) ?: return@withContext null
//            val filePath =
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    getRealPathFromURI(context, contentUri) ?: ""
//                } else contentUri.path ?: ""
//
//        }



    suspend fun uploadPhotoToStorage(localPhotoUri: Uri,photo: Photo)  {
        val storageRef = fireStorage.child ("images/${Firebase.auth.uid}/${photo.photoFileName} ")
        val uploadTask = storageRef.putFile(localPhotoUri).await()

       if (uploadTask.task.isSuccessful){
           val uri = uploadTask.storage.downloadUrl.await()
           savePhotoUrlToFirestore(photo,uri)
       }

    }

    suspend fun uploadVideoToStorage(localVideoUri: Uri,video: Video)  {
        val storageRef = fireStorage.child ("videos/${Firebase.auth.uid}/${video.videoFileName} ")
        val uploadTask = storageRef.putFile(localVideoUri).await()

        if (uploadTask.task.isSuccessful){
            val uri = uploadTask.storage.downloadUrl.await()
            saveVideoUrlToFirestore(video,uri)
        }

    }

     private fun saveVideoUrlToFirestore(video: Video, uri:Uri){
        Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .update("videosUrl", FieldValue.arrayUnion(video))
    }


    private fun savePhotoUrlToFirestore(photo: Photo, uri:Uri){
        Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .update("imagesUrl", FieldValue.arrayUnion(photo))
    }




    fun getPhotoFile(model: Photo):File = File(fileDir,model.photoFileName)

    fun getVideoFile(model: Video):File = File(fileDir , model.videoFileName)





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