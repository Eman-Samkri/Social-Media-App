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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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




    suspend fun uploadProfilePhotoToStorage(localPhotoUri: Uri) :String? {
        val storageRef = fireStorage.child ("images/${Firebase.auth.uid}")
        val uploadTask = storageRef.putFile(localPhotoUri).await()
//todo not working yet
       if (uploadTask.task.isSuccessful){
           val uri = uploadTask.storage.downloadUrl.await()
           return uri.toString()
           //savePhotoUrlToFirestore(uri)
       }
        return null

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


//    private fun savePhotoUrlToFirestore(uri:Uri){
//        Firebase.firestore.collection("users")
//            .document(Firebase.auth.currentUser?.uid!!)
//            .update("profilePictureUrl", uri)
//
//    }

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

    private val messagesList: MutableList<ChatMessage> = mutableListOf()
    private val messagesMutableLiveData = MutableLiveData<List<ChatMessage>>()

    fun loadChatMessages(senderId: String, receiverId: String): MutableLiveData<List<ChatMessage>> {

        if (messagesMutableLiveData.value != null) return messagesMutableLiveData

        Firebase.firestore.collection("RoomMassage")
            .addSnapshotListener(EventListener { querySnapShot, firebaseFirestoreException ->
            if (firebaseFirestoreException == null) {
                messagesList.clear()//clear message list so won't get duplicated with each new message
                querySnapShot?.documents?.forEach {
                    if (it.id == "${senderId}_${receiverId}"
                        || it.id == "${receiverId}_${senderId}") {
                        it.get("messages")
                        }
                        if (!messagesList.isNullOrEmpty())
                            messagesMutableLiveData.value = messagesList
                    }

                }
            }
        )

        return messagesMutableLiveData
    }



    fun sendMessage(senderId: String, receiverId: String,message: ChatMessage) {
        //todo add last message date field to chat members document so we can sort home chats with

        //one node for the same chat
        Firebase.firestore.collection("RoomMassage").document("${senderId}_${receiverId}").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    //this node exists send your message
                    Firebase.firestore.collection("RoomMassage").document("${senderId}_${receiverId}")
                        .update("messages", FieldValue.arrayUnion(message))

                } else {
                    // check receiverId_senderId
                    Firebase.firestore.collection("RoomMassage").document("${receiverId}_${senderId}").get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                Firebase.firestore.collection("RoomMassage").document("${receiverId}_${senderId}")
                                    .update("messages", FieldValue.arrayUnion(message))
                            } else {
                                // receiverId_senderId both don't exist
                                Firebase.firestore.collection("RoomMassage").document("${senderId}_${receiverId}")
                                    .set("messages" to mutableListOf<ChatMessage>())
                                    .addOnSuccessListener {
                                        Firebase.firestore.collection("RoomMassage").document("${senderId}_${receiverId}")
                                            .update("messages", FieldValue.arrayUnion(message))

                                        //add ids of chat members
                                        Firebase.firestore.collection("RoomMassage").document("${senderId}_${receiverId}").update("chat_members",
                                                FieldValue.arrayUnion(senderId, receiverId)
                                            )
                                    }
                            }


                        }
                }
            }

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