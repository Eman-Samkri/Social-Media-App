package com.t1000.capstone21.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.ItemSmallVideoBinding
import com.t1000.capstone21.databinding.ProfileFragmentBinding
import com.t1000.capstone21.models.NotificationData
import com.t1000.capstone21.models.Video
import com.t1000.capstone21.notification.FirebaseService
import com.t1000.capstone21.notification.PushNotification
import com.t1000.capstone21.notification.RetrofitInstance
import com.t1000.capstone21.ui.comment.RvDiffUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TOPIC = "/topics/myTopic2"
private const val TAG = "ProfileFragment"


class ProfileFragment : Fragment() {


    private val viewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }


    private lateinit var binding : ProfileFragmentBinding

    private val args: ProfileFragmentArgs by navArgs()

    private lateinit var userOwnProfileId:String

    private lateinit var currentUser:String

    private lateinit var userName:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        FirebaseService.sharedPref = requireActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        setHasOptionsMenu(true)

        if (FirebaseAuth.getInstance().currentUser?.uid == null){
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

        binding.smallVideo.layoutManager = GridLayoutManager(context,3)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //the own profile or just show your profile
        userOwnProfileId = if (args.currentUserId != null){
            args.currentUserId.toString()
        }else{
            currentUser
        }
        //own profile
        if (FirebaseAuth.getInstance().currentUser?.uid!! == args.currentUserId
            || args.currentUserId == null){
            binding.follwingBtn.visibility = View.GONE
        }

        viewModel.fetchUserById(userOwnProfileId).observe(
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
                        userName = user.username

                    }

                }
            }
        )

        viewModel.fetchVideosUser(userOwnProfileId).observe(
            viewLifecycleOwner, Observer{
                binding.smallVideo.adapter = VideosAdapter(it)

            })


        binding.follwingBtn.setOnClickListener {
            viewModel.addFollowing(userOwnProfileId)
            viewModel.sendNotificationToUser(userOwnProfileId, userName,"some one following")
            binding.follwingBtn.visibility = View.GONE
            binding.unFollowBtn.visibility = View.VISIBLE

        }

        binding.unFollowBtn.setOnClickListener {
            viewModel.unFollow(userOwnProfileId)
            binding.follwingBtn.visibility = View.VISIBLE
            binding.unFollowBtn.visibility = View.GONE
            viewModel.sendNotificationToUser(userOwnProfileId, userName,"unfollow to you")

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

    private inner class VideoHolder(val binding: ItemSmallVideoBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(video: Video){

//            binding.smallVideoVidw.pre
//            setVideoPath(video.videoUrl)
//
//            binding.profileVideoView.start()






        }

    }

    private inner class VideosAdapter(val videos:List<Video>):
        RecyclerView.Adapter<VideoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
            val binding = ItemSmallVideoBinding.inflate(
                layoutInflater,
                parent,
                false
            )

            return VideoHolder(binding)

        }

        override fun onBindViewHolder(holder: VideoHolder, position: Int) {
            val videoItem : Video = videos[position]
            holder.bind(videoItem)
        }

        override fun getItemCount(): Int = videos.size


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.signout,menu)


        val singOutItem =menu.findItem(R.id.signoutMenu)
        singOutItem.actionView



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.signoutMenu -> {
                FirebaseAuth.getInstance().signOut()
                val action = ProfileFragmentDirections.actionProfileFragmentToNavigationMe()
                findNavController().navigate(action)

                true
            }

            else -> super.onOptionsItemSelected(item)


        }
    }


}