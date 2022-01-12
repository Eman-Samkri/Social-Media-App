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

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Todo: not pass the viewModel
        viewModel.fetchVideosUser(args.videoId.toString()).observe(
            viewLifecycleOwner, Observer {
               it.forEach {
                   binding.homeVideoView.setVideoPath(it.videoUrl)
                   Log.e(TAG, "onViewCreated: ${it.videoId}", )
                   binding.homeVideoView.start()
               }
            })



    }
}