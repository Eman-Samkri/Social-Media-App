package com.t1000.capstone21.ui.me

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.t1000.capstone21.databinding.ProfileFragmentBinding


private const val TAG = "ProfileFragment"
class ProfileFragment : Fragment() {


    private val viewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }


    private lateinit var binding : ProfileFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "setupLiveData: iiiiiiiiiiiiiiiiiiii")
        viewModel.fetchUser().observe(
            viewLifecycleOwner, Observer {
                Log.e(TAG, "setupLiveData: $it")
                it?.let {
                    binding.followersCountNumber.text = it.followers.toString()
                    binding.followingCountNumber.text = it.following.toString()
                    binding.userNameTv.text = it.username.toString()
                    Log.e(TAG, "setupLiveData: $it")
                }
            }
        )
    }



//         val user = viewModel.fetchUser()
//        binding.userName.text = user.
//        //binding.profileVideoView.setVideoPath()
//       // binding.profileVideoView.start()
//        Log.d(TAG, "hhhhhhhhhhhhhhhhhhh")



       // setUpLiveData()
//
//         fireStore
//            .collection("users")
//            .document(Firebase.auth.currentUser?.uid!!)
//            .get()
//             .await().toObject()
//
//
//
////            .addOnSuccessListener {
////                val userData = it.data
////                    userData?.forEach {
////                    when(it.key){
////                        "username" -> {
////                            binding.userName.text = it.value.toString()
////                            Log.d(TAG, "${it.value}")
////                        }
////
////                    }


               // binding.userTag.text = userData?.get("username").toString()

//                Log.d(TAG, "${it.data?.get("username")}")
//                val videoFile = it.data?.get("videosUrl")


//                binding.userTag.text = it["username"]
//                    .toString()
                //val uri = it["videosUrl"]

//                val uri = it.getString("videoUrl") as Uri



//    }
//
//    fun setupLiveData() {
//
//
//    }

}