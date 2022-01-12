package com.t1000.capstone21.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.t1000.capstone21.databinding.ItemHomeVideoBinding

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

        viewModel.fetchVideosUser(args.videoId.toString()).observe(
            viewLifecycleOwner, Observer {
               it.forEach {
                   binding.homeVideoView.setVideoPath(it.videoUrl)
                   binding.homeVideoView.start()
               }
            })



    }
}