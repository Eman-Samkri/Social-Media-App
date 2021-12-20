package com.t1000.capstone21

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.t1000.capstone21.models.Photo
import com.t1000.capstone21.models.User
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

private const val TAG = "Repo"
class Repo private constructor(context: Context) {



    val auth = FirebaseAuth.getInstance()

    val fileDir : File = context.applicationContext.filesDir

    private val fireStorage = Firebase.storage.reference

    private val fireStore = Firebase.firestore

    val currentUserId = auth.currentUser?.uid


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

    private fun addVideoToVideoCollection(video: Video){

        Firebase.firestore.collection("video")
            .add(video)

    }
     private fun saveVideoUrlToFirestore(video: Video, uri:Uri){
         video.videoUrl = uri.toString()

        val user = User(userId = currentUserId.toString())
         video.username = user.username
        Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .update("videosUrl", FieldValue.arrayUnion(video))


         addVideoToVideoCollection(video)

     }


    private fun savePhotoUrlToFirestore(photo: Photo, uri:Uri){
        photo.photoUrl = uri.toString()

        Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .update("imagesUrl", FieldValue.arrayUnion(photo))

    }




    fun getPhotoFile(model: Photo):File = File(fileDir,model.photoFileName)

    fun getVideoFile(model: Video):File = File(fileDir , model.videoFileName)

    //suspend fun fetchUserProfile():List<User> = getUserProfile()


//    suspend fun getUserProfile() {
//        fireStore
//            .collection("users")
//            .document(Firebase.auth.currentUser?.uid!!)
//            .get()
//            .addOnSuccessListener {
////                binding.userTag.text = it["username"].toString()
////                Toast.makeText(context,"${it["username"]} ## ", Toast.LENGTH_LONG).show()
//                val user = it.get("username",User::class.java)
//                Log.d(TAG, "${it["username"]}} => == $user")
//            }
//            .addOnFailureListener { exception ->
////                Log.w(ContentValues.TAG, "Error getting documents.", exception)
//            }.await()
//
//    }

//    private suspend fun fetchVideoMetaData():List<Video>{
//        var videoItems:List<Video> = emptyList()
//
//        val refrence= fireStore.document().
//            .awaitResponse()
//
//        if (response.isSuccessful){
//            galleryItems = response.body()?.photos?.galleryItems ?: emptyList()
//            galleryItems = galleryItems.filterNot { it.url.isBlank() }
//
//
//        }else{
//            Log.e(TAG , "something gone wrong ${response.errorBody()}")
//        }
//
//        return galleryItems
//
//    }


     suspend fun fetchRandomVideos() : List<Video> {

         val videos = fireStore.collection("video")
               .get()
             .addOnFailureListener { exception ->
                 Log.d(TAG, "Error getting documents: ", exception)
             }
             .addOnSuccessListener { result ->
                 for (document in result) {
                     Log.d(TAG, "${document.id} => ${document.data}")
                 }
             }.await()
             .toObjects(Video::class.java)

         return videos

    }


    suspend fun fetchUserVideo(): Video? {

        val video = fireStore
            .collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .get()
            .addOnFailureListener {  }
            .await()
            .toObject(Video::class.java)

        return video






//           .addOnSuccessListener {
//                val userData = it.data
//                    userData?.forEach {
//                    when(it.key){
//                        "username" -> {
//                            binding.userName.text = it.value.toString()
//                            Log.d(TAG, "${it.value}")
//                        }
//
//                    }
    }


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