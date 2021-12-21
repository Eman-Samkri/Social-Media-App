package com.t1000.capstone21.ui.home

import android.Manifest
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.FragmentHomeBinding
import com.t1000.capstone21.databinding.ItemHomeVideoBinding
import com.t1000.capstone21.models.User
import com.t1000.capstone21.models.Video


private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {



    private val viewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }


    private lateinit var binding:FragmentHomeBinding


//    private val getPermissionLuncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ){
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
  //      getPermissionLuncher.launch(Manifest.permission.RECORD_AUDIO)
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
           // binding.textView.text = video.videoFileName
            binding.usernameText.text = video.username

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
                // device has the app and make an intent to the app. For now, just create a link to the firebase video
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_TEXT, video.videoUrl)
                binding.root.context.startActivity(intent)
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





}