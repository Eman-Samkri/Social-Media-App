package com.t1000.capstone21.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.t1000.capstone21.databinding.ItemHomeVideoBinding

private const val TAG = "VidePostFragment"
class VidePostFragment:Fragment() {

    private lateinit var binding :ItemHomeVideoBinding

    private val args: VidePostFragmentArgs by navArgs()

    private val viewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ItemHomeVideoBinding.inflate(layoutInflater)

        binding.commentL.visibility = View.GONE
        binding.shard.visibility =View.GONE
        binding.like.visibility =View.GONE
        binding.deleteVideo.visibility = View.VISIBLE
        binding.delete.visibility = View.VISIBLE
        binding.deletL.visibility = View.VISIBLE

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Todo: not pass the viewModel
        viewModel.fetchVideosUser(args.userId.toString()).observe(
            viewLifecycleOwner, Observer {
               it.forEach { video ->
                   binding.homeVideoView.setVideoPath(video.videoUrl)

                   binding.homeVideoView.setOnPreparedListener {
                       //binding.progressBar.visibility = View.GONE
                       it.start()

                       val videoRatio = it.videoWidth / it.videoHeight.toFloat()
                       val screenRatio = binding.homeVideoView.width / binding.homeVideoView.height.toFloat()
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
               }
            })


        binding.deleteVideo.setOnClickListener {
            viewModel.deleteVideo(args.videoId.toString())
        }

    }
}