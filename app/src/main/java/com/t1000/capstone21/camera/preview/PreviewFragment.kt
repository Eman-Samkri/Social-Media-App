package com.t1000.capstone21.camera.preview

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.R
import com.t1000.capstone21.camera.baseFragment.CameraViewModel
import com.t1000.capstone21.camera.videoFragment.VideoFragmentDirections
import com.t1000.capstone21.databinding.PreviewFragmentBinding
import com.t1000.capstone21.models.Video
import com.t1000.capstone21.ui.profile.ProfileViewModel

private const val TAG = "PreviewFragment"
class PreviewFragment : Fragment() {

    private lateinit var binding: PreviewFragmentBinding
    private val args: PreviewFragmentArgs by navArgs()
    private val viewModel by lazy { ViewModelProvider(this).get(CameraViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = PreviewFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e(TAG, "onViewCreated: ${args.savedUri}", )
        binding.preview.setVideoURI(args.savedUri.toUri())
        binding.preview.start()

        binding.uplodToFirebase.setOnClickListener {
            val video = Video()
            video.userId = FirebaseAuth.getInstance().currentUser?.uid!!
            viewModel.uploadVideo(args.savedUri.toUri(),video)
            findNavController().navigate(R.id.navigation_home)
        }

    }


}