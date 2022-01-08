package com.t1000.capstone21.ui.home

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.databinding.FragmentHomeBinding
import com.t1000.capstone21.databinding.ItemHomeVideoBinding
import com.t1000.capstone21.models.Video


private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {



    private val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }


    private lateinit var binding:FragmentHomeBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (FirebaseAuth.getInstance().currentUser == null){

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.myRv.layoutManager = LinearLayoutManager(context)
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding.myRv)

        return binding.root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchRandomVideos().observe(
            viewLifecycleOwner, Observer{
                Log.e(TAG, "onViewCreated: list $it ")

                binding.myRv.adapter = VideosAdapter(it)

        })

    }


    private inner class HomeVideoHolder(val binding:ItemHomeVideoBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(video:Video){
            val user = viewModel.fetchUserById(video.userId)
                user.observe(
                viewLifecycleOwner, Observer{
                    it.forEach {
                       binding.usernameTv.text = it.username
                        binding.imageUser.load(it.profilePictureUrl)
                    }

                })
            binding.likeTV.text = video.likes.toString()

            binding.homeVideoView.setVideoPath(video.videoUrl)

            binding.homeVideoView.setOnPreparedListener {
                binding.progressBar.visibility = View.GONE
                it.start()

                val videoRatio =
                    it.videoWidth / it.videoHeight.toFloat()
                val screenRatio =
                    binding.homeVideoView.width / binding.homeVideoView.height.toFloat()
                val scale = videoRatio / screenRatio

                if (scale >= 1f) {
                    binding.homeVideoView.scaleX = scale
                } else {
                    binding.homeVideoView.scaleY = 1f / scale
                }
            }

            binding.homeVideoView.setOnCompletionListener {
                it.start()
            }

            binding.sharVideoBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, video.videoUrl)
                binding.root.context.startActivity(intent)

            }

            binding.commentVideoBtn.setOnClickListener {
                val action = HomeFragmentDirections.actionNavigationHomeToCommentFragment(video.videoId,video.userId)
                findNavController().navigate(action)
            }

            binding.addLikeBtn.setOnClickListener {
                if (FirebaseAuth.getInstance().currentUser?.uid == null){
                    val action = HomeFragmentDirections.actionNavigationHomeToNavigationMe()
                    findNavController().navigate(action)
                }else {
                    viewModel.addLike(video)
                    binding.likeTV.text = video.likes.toString()
                }
            }

            binding.usernameTv.setOnClickListener {
              toUserProfile(video)
            }

            binding.imageUser.setOnClickListener {
                toUserProfile(video)
            }


        }

    }


    private inner class VideosAdapter(val videos:List<Video>):
        RecyclerView.Adapter<HomeVideoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeVideoHolder {
            val binding = ItemHomeVideoBinding.inflate(
                layoutInflater,
                parent,
                false
            )

            return HomeVideoHolder(binding)

        }

        override fun onBindViewHolder(holder: HomeVideoHolder, position: Int) {
            val videoItem : Video = videos[position]
            holder.bind(videoItem)
        }

        override fun getItemCount(): Int = videos.size


    }

    private fun toUserProfile(video:Video){
        if (FirebaseAuth.getInstance().currentUser?.uid == null){
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationMe()
            findNavController().navigate(action)
        }else {
            val action =
                HomeFragmentDirections.actionNavigationHomeToProfileFragment(video.userId)
            Log.e(TAG, "bind: ${video.userId}",)
            findNavController().navigate(action)
        }
    }





}