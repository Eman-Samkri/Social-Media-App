package com.t1000.capstone21.ui.me

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.t1000.capstone21.databinding.ProfileFragmentBinding
import com.t1000.capstone21.ui.sticker.StickerFragmentArgs


private const val TAG = "ProfileFragment"
class ProfileFragment : Fragment() {


    private val viewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }


    private lateinit var binding : ProfileFragmentBinding

    private val args: ProfileFragmentArgs by navArgs()

    private lateinit var userId:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = if (args.currentVideoId != null){
            args.currentVideoId.toString()
        }else{
            FirebaseAuth.getInstance().currentUser?.uid!!
        }
        Log.e(TAG, "onViewCreated: profile $userId", )
        viewModel.fetchUserById(userId).observe(
            viewLifecycleOwner, Observer {
                Log.e(TAG, "setupLiveData: $it")
                it?.let {
                    it.forEach {
                        //                    binding.followersCountNumber.text = it.followers.toString()
//                    binding.followingCountNumber.text = it.following.toString()
                        binding.userNameTv.text = it.username.toString()
                        Log.e(TAG, "setupLiveData: $it")
                    }

                }
            }
        )
    }


}