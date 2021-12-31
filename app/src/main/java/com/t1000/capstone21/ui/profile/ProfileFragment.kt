package com.t1000.capstone21.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.ProfileFragmentBinding

import com.t1000.capstone21.ui.sticker.StickerFragmentArgs


private const val TAG = "ProfileFragment"
class ProfileFragment : Fragment() {


    private val viewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }


    private lateinit var binding : ProfileFragmentBinding

    private val args: ProfileFragmentArgs by navArgs()

    private lateinit var userId:String

    private lateinit var currentUser:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null){
            findNavController().navigate(R.id.loginUserFragment)

        }else{
            currentUser = Firebase.auth.currentUser!!.uid
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(layoutInflater)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = if (args.currentUserId != null){
            args.currentUserId.toString()
        }else{
            currentUser
        }

        if (FirebaseAuth.getInstance().currentUser?.uid!! == args.currentUserId){
            binding.follwingBtn.visibility = View.GONE
        }

        viewModel.fetchUserById(userId).observe(
            viewLifecycleOwner, Observer {
                it?.let {
                    it.forEach { user->
                        if (user.followers.contains(currentUser)){
                            binding.follwingBtn.visibility = View.GONE
                            binding.unFollowBtn.visibility = View.VISIBLE
                        }

                        binding.followersCountNumber.text = user.followers.distinct().count().toString()
                        binding.followingCountNumber.text = user.following.distinct().count().toString()
                        binding.userNameTv.text = user.username
                    }

                }
            }
        )


        binding.follwingBtn.setOnClickListener {
            viewModel.addFollowing(userId)
            binding.follwingBtn.visibility = View.GONE
            binding.unFollowBtn.visibility = View.VISIBLE
        }

        binding.unFollowBtn.setOnClickListener {
            viewModel.unFollow(userId)
            binding.follwingBtn.visibility = View.VISIBLE
            binding.unFollowBtn.visibility = View.GONE
        }

        binding.followersCountNumber.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToFollowFragment(args.currentUserId,false)
            findNavController().navigate(action)
        }

        binding.followingCountNumber.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToFollowFragment(args.currentUserId,true)
            findNavController().navigate(action)
        }
    }


}