package com.t1000.capstone21


import android.content.Context
import android.net.Uri
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.t1000.capstone21.models.Comment
import com.t1000.capstone21.models.Photo
import com.t1000.capstone21.models.User
import com.t1000.capstone21.models.Video
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.math.log

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
            addVideoToVideoCollection(video,uri)
        }

    }

    private fun addVideoToVideoCollection(video: Video,uri:Uri){
        video.videoUrl = uri.toString()

        Firebase.firestore.collection("video")
            .document(video.videoId)
            .set(video)
    }


    private fun savePhotoUrlToFirestore(photo: Photo, uri:Uri){
        photo.photoUrl = uri.toString()

        Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .update("imagesUrl", FieldValue.arrayUnion(photo))

    }

    fun addLike(video:Video){
        Firebase.firestore.collection("video")
            .document(video.videoId)
            .update("likes", ++video.likes)

    }



      suspend fun saveCommentToFirestore(videoId:String,comment:Comment){
          Firebase.firestore.collection("video")
              .document(videoId)
              .get()
              .addOnSuccessListener {
                  Log.d(TAG, "addOnSuccessListener: $it")
                  val videoData = it.toObject(Video::class.java)

                  val mutableCommentList = mutableListOf<Comment>()
                  //original comment list
                  videoData?.comments?.forEach {
                      mutableCommentList += it
                  }
                  mutableCommentList.add(comment)

                  Firebase.firestore.collection("video").document(videoId)
                      .update("comments", mutableCommentList)

              }

      }

    suspend fun addFollowing(userId :String){

        val currUser = Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .get()
            .await()
            .toObject(User::class.java)

        val mutableFollowingList = mutableListOf<String>()
        //original comment list
        currUser?.following?.forEach {
            mutableFollowingList += it
        }
        mutableFollowingList.add(userId)

        Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
            .update("following", mutableFollowingList)
    }


    fun getPhotoFile(model: Photo):File = File(fileDir,model.photoFileName)

    fun getVideoFile(model: Video):File = File(fileDir , model.videoFileName)

    suspend fun fetchUser(): User {
       val user = fireStore
            .collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .get()
            .await()
            .toObject(User::class.java)

        //TODO:remove !! to avoid the bug
        return user!!
    }


    suspend fun fetchUserById(userId:String): User {
        val user = fireStore
            .collection("users")
            .whereEqualTo("userId",userId)
            .get()
            .await()
            .toObjects(User::class.java)

        return user[0]
    }



     suspend fun fetchRandomVideos() : List<Video> {
         val videos = fireStore.collection("video")
               .get()
            .await()
             .toObjects(Video::class.java)

         return videos

    }

    suspend fun fetchVideosById(videoId:String) : List<Video> {
        val video = fireStore.collection("video")
            .whereEqualTo("videoId",videoId)
            .get()
           .await()
            .toObjects(Video::class.java)
        return video
    }

    suspend fun fetchVideosCommentById(videoId:String):List<Video> {
//        val video = fireStore.collection("video")
//            .whereEqualTo("videoId",videoId)
        val video =
            FirebaseFirestore
                .getInstance()
                .collection("video")
                .get()
                .await()
                .toObjects(Video::class.java)

        for (document in video) {

//            Log.d("MainActivity ${document.comments}  ")
        }

        return video

    }

    suspend fun deleteVideoComment(videoId:String, index: Int){
        val video = Firebase.firestore.collection("video")
            .document(videoId)
            .get()
            .await()
            .toObject(Video::class.java)


              val mutableCommentList = mutableListOf<Comment>()
                //original comment list
              video?.comments?.forEach {
                  mutableCommentList += it
            }

        mutableCommentList.remove(mutableCommentList[index])

        Firebase.firestore.collection("video").document(videoId)
            .update("comments", mutableCommentList)
            .await()
    }


    //TODO: use this
    suspend fun deleteVideo(videoId:String){
        Firebase.firestore.collection("video")
            .document(videoId)
            .delete()
            .await()
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