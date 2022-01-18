package com.t1000.capstone21.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.ItemSmallVideoBinding
import com.t1000.capstone21.databinding.ProfileFragmentBinding
import com.t1000.capstone21.models.Video
import com.t1000.capstone21.notification.FirebaseService
import java.util.*

const val TOPIC = "/topics/myTopic2"
private const val TAG = "ProfileFragment"


class ProfileFragment : Fragment() {


    private val viewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }


    private lateinit var binding : ProfileFragmentBinding

    private val args: ProfileFragmentArgs by navArgs()

    private lateinit var userOwnProfileId:String

    private lateinit var currentUser:String

    private var userName:String =""





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadLocate(requireActivity()) // call LoadLocate

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
        }
        FirebaseService.sharedPref = context?.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

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

        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(R.id.profileEditFragment)
        }

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
            binding.followingBtn.visibility = View.GONE
            binding.editProfileBtn.visibility =View.VISIBLE
        }

        viewModel.fetchUserById(userOwnProfileId).observe(
            viewLifecycleOwner, Observer {
                it?.let {
                    it.forEach { user->
                        if (user.followers.contains(currentUser)){
                            binding.followingBtn.visibility = View.GONE
                            binding.unFollowBtn.visibility = View.VISIBLE
                        }

                        binding.followersCountNumber.text = user.followers.distinct().count().toString()
                        binding.followingCountNumber.text = user.following.distinct().count().toString()
                        binding.userNameTv.text = user.username
                        binding.userPhoto.load(user.profilePictureUrl)
                        userName = user.username

                    }

                }
            }
        )

        viewModel.fetchVideosUser(userOwnProfileId).observe(
            viewLifecycleOwner, Observer{
                binding.smallVideo.adapter = VideosAdapter(it)
                if (userOwnProfileId != currentUser){
                    viewModel.sendNotificationToUser(userOwnProfileId, userName,"some one visit your profile")
                }


            })


        binding.followingBtn.setOnClickListener {
            viewModel.addFollowing(userOwnProfileId)
            viewModel.sendNotificationToUser(userOwnProfileId, userName,"some one following")
            binding.followingBtn.visibility = View.GONE
            binding.unFollowBtn.visibility = View.VISIBLE

        }

        binding.unFollowBtn.setOnClickListener {
            viewModel.unFollow(userOwnProfileId)
            binding.followingBtn.visibility = View.VISIBLE
            binding.unFollowBtn.visibility = View.GONE
            viewModel.sendNotificationToUser(userOwnProfileId, userName,"unfollow to you")

        }

        binding.followersCountNumber.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToFollowFragment(args.currentUserId, 1.0F)
            findNavController().navigate(action)
        }

        binding.followingCountNumber.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToFollowFragment(args.currentUserId,2.0F)
            findNavController().navigate(action)
        }
    }

    private inner class VideoHolder(val binding: ItemSmallVideoBinding): RecyclerView.ViewHolder(binding.root),View.OnClickListener{

        private lateinit var videoId :String

        fun bind(video: Video){
            binding.loadingBar.visibility = View.GONE
            binding.smallVideoView.setVideoPath(video.videoUrl)
            binding.smallVideoView.seekTo(3)
            videoId = video.videoId

        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v == itemView){
                val action = ProfileFragmentDirections.actionProfileFragmentToVidePostFragment(userOwnProfileId,videoId)
                findNavController().navigate(action)
            }
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

            R.id.langMenu-> {
                showChangeLang()
                true
            }

            else -> super.onOptionsItemSelected(item)


        }
    }


    private fun showChangeLang() {

        val listItems = arrayOf("عربي","English")

        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setTitle("Choose Language")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialog, which ->
            if (which == 0) {
                setLocate("ar",requireActivity())
                recreate(requireActivity())
            } else if (which == 1) {
                setLocate("en",requireActivity())
                recreate(requireActivity())
            }

            dialog.dismiss()
        }
        val mDialog = mBuilder.create()

        mDialog.show()

    }



    companion object{

        fun setLocate(Lang: String,activity: Activity) {

            val locale = Locale(Lang)
            Locale.setDefault(locale)

            val config = Configuration()
            config.locale = locale
            activity.resources.updateConfiguration(config, activity.resources.displayMetrics)

            val editor = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
            editor.putString("Current_Lang", Lang)
            editor.apply()
        }

        fun loadLocate(activity: Activity) {
            val sharedPreferences = activity.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
            val language = sharedPreferences.getString("Current_Lang", "")
            if (language != null) {
                setLocate(language,activity)
            }
        }
    }


}