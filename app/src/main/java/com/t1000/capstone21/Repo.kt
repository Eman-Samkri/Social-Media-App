package com.t1000.capstone21


import android.content.Context
import android.net.Uri
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.t1000.capstone21.models.*
import com.t1000.capstone21.notification.PushNotification
import com.t1000.capstone21.notification.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.HashMap
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

       val uplodToFirestore= Firebase.firestore.collection("video")
            .document(video.videoId)
            .set(video)

        if (uplodToFirestore.isSuccessful){

        }

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
//TODO :Using add snapshot listener to update
    fun fetchVideosCommentById(videoId:String, comment:Comment) {
         fireStore.collection("video")
            .whereEqualTo("videoId",videoId)
            .addSnapshotListener (EventListener { querySnapShot, firebaseFirestoreException ->
                if (firebaseFirestoreException == null) {
                    querySnapShot?.documents?.forEach {
                        val videoData = it.toObject(Video::class.java)
                    videoData?.comments?.forEach {

                    }
                    }
                }
            })


//    { querySnapshot, error ->
//                if (error != null) {
//                    Log.e("FIRESTORE", "CommentListener error.", error)
//                    return@addSnapshotListener
//                }
//                val items = mutableListOf<Comment>()
//                querySnapshot!!.documents.forEach {
//                    val videoData = it.toObject(Video::class.java)
//                    videoData?.comments?.forEach {
//                        items += it
//                    }
//                    items.add(comment)
//
//                    return@forEach
//                }
//            }


    }


     fun sendNotificationToUser(userId :String, title:String ,message:String) {
        Firebase.firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                Log.e(TAG, "onViewCreated: token ${it.get("token")}",)

                PushNotification(
                    NotificationData(title, message),
                    it.get("token").toString()
                ).also {
                    sendNotification(it)
                }
            }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                //  Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
            }
        } catch(e: Exception) {
            Log.e(TAG, "jhhhhhhhhhhh",e)
        }
    }

    suspend fun addFollowing(userId :String){

        val currUser = Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .get()
            .await()
            .toObject(User::class.java)

        val mutableFollowingList = mutableListOf<String>()
        currUser?.following?.forEach {
            mutableFollowingList += it
        }
        mutableFollowingList.add(userId)

        Firebase.firestore.collection("users").document(Firebase.auth.currentUser?.uid!!)
            .update("following", mutableFollowingList)

        //------------------------------------

        val followUser = Firebase.firestore.collection("users")
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)

        val mutableFollowersList = mutableListOf<String>()
        followUser?.followers?.forEach {
            mutableFollowersList += it
        }
        mutableFollowersList.add(Firebase.auth.currentUser?.uid!!)

        Firebase.firestore.collection("users")
            .document(userId)
            .update("followers", mutableFollowersList)
    }


    fun getPhotoFile(model: Photo):File = File(fileDir,model.photoFileName)

    fun getVideoFile(model: Video):File = File(fileDir , model.videoFileName)



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




    suspend fun fetchVideosUser(userId:String) : List<Video> {
        val video = fireStore.collection("video")
            .whereEqualTo("userId",userId)
            .get()
            .await()
            .toObjects(Video::class.java)
        return video
    }
//    suspend fun fetchVideosCommentById(videoId:String):List<Video> {
////        val video = fireStore.collection("video")
////            .whereEqualTo("videoId",videoId)
//        val video =
//            FirebaseFirestore
//                .getInstance()
//                .collection("video")
//                .get()
//                .await()
//                .toObjects(Video::class.java)
//
//        for (document in video) {
//
////            Log.d("MainActivity ${document.comments}  ")
//        }
//
//        return video
//
//    }

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

    suspend fun deleteUserFollow(userId:String){
        val currentUser = Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .get()
            .await()
            .toObject(User::class.java)

        val mutableFollowList = mutableListOf<String>()
        currentUser?.followers?.forEach {
            mutableFollowList += it
        }

        mutableFollowList.remove(userId)

        Firebase.firestore.collection("users")
            .document(Firebase.auth.currentUser?.uid!!)
            .update("following", mutableFollowList)
            .await()
//-----------------------------------------------------------------
        val unfollowUser = Firebase.firestore.collection("users")
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)


        val mutableFollowingList = mutableListOf<String>()
        unfollowUser?.following?.forEach {
            mutableFollowingList += it
        }

        mutableFollowingList.remove(Firebase.auth.currentUser?.uid!!)

        Firebase.firestore.collection("users")
            .document(userId)
            .update("followers", mutableFollowingList)
            .await()


    }


    //TODO: use this
    suspend fun deleteVideo(videoId:String){
        Firebase.firestore.collection("video")
            .document(videoId)
            .delete()
            .await()
    }

//--------------------------------------------------------

    private val messageCollectionReference = Firebase.firestore.collection("RoomMassage")
    private val messagesList: MutableList<ChatMessage> = mutableListOf<ChatMessage>()
    private val chatFileMapMutableLiveData = MutableLiveData<Map<String, Any?>>()
    private val messagesMutableLiveData = MutableLiveData<List<ChatMessage>>()

    fun loadChatMessages(chatMassage:ChatMessage, senderId: String?, receiverId: String): LiveData<List<ChatMessage>> {

        if (messagesMutableLiveData.value != null) return messagesMutableLiveData

            messageCollectionReference.addSnapshotListener(EventListener { querySnapShot, firebaseFirestoreException ->
            if (firebaseFirestoreException == null) {
                messagesList.clear()//clear message list so won't get duplicated with each new message
                querySnapShot?.documents?.forEach {
                    if (it.id == "${senderId}_${receiverId}" || it.id == "${receiverId}_${senderId}") {
                        //this is the chat document we should read messages array
                        val messagesFromFirestore = it.get("messages") as List<HashMap<String, Any>>?
                            ?: throw Exception("My cast can't be done")
                        messagesFromFirestore.forEach { messageHashMap ->

                            val message = when (messageHashMap["type"] as String) {
                                "Text"-> {
                                    chatMassage.type = "Text"

                                }
                                "Image" -> {

                                }
                                "File" -> {

                                }
                                "Voice" -> {

                                }
                                else -> {
                                    throw Exception("unknown type")
                                }
                            }


                           // messagesList.add(message)
                        }

                        if (!messagesList.isNullOrEmpty())
                            messagesMutableLiveData.value = messagesList
                    }

                }
            }
        })

        return messagesMutableLiveData
    }




//    fun sendMessage(message: ChatMessage, senderId: String?,  receiverId: String) {
//
//        messageCollectionReference.document("${senderId}_${receiverId}").get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    //this node exists send your message
//                    messageCollectionReference.document("${senderId}_${receiverId}")
//                        .update("messages", FieldValue.arrayUnion(message.serializeToMap()))
//
//                } else {
//                    //senderId_receiverId node doesn't exist check receiverId_senderId
//                    messageCollectionReference.document("${receiverId}_${senderId}").get()
//                        .addOnSuccessListener { documentSnapshot2 ->
//
//                            if (documentSnapshot2.exists()) {
//                                messageCollectionReference.document("${receiverId}_${senderId}")
//                                    .update(
//                                        "messages",
//                                        FieldValue.arrayUnion(message.serializeToMap())
//                                    )
//                            } else {
//                                //no previous chat history(senderId_receiverId & receiverId_senderId both don't exist)
//                                //so we create document senderId_receiverId then messages array then add messageMap to messages
//                                messageCollectionReference.document("${senderId}_${receiverId}")
//                                    .set(
//                                        mapOf("messages" to mutableListOf<ChatMessage>()),
//                                        SetOptions.merge()
//                                    ).addOnSuccessListener {
//                                        //this node exists send your message
//                                        messageCollectionReference.document("${senderId}_${receiverId}")
//                                            .update("messages", FieldValue.arrayUnion(message.serializeToMap())
//                                            )
//
//                                        //add ids of chat members
//                                        messageCollectionReference.document("${senderId}_${receiverId}")
//                                            .update("chat_members", FieldValue.arrayUnion(senderId, receiverId)
//                                            )
//
//                                    }
//                            }
//                        }
//                }
//            }
//
//    }



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