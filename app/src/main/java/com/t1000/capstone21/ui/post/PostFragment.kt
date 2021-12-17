package com.t1000.capstone21.ui.post

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.t1000.capstone21.R
import com.t1000.capstone21.camera.PermissionsFragmentDirections
import com.t1000.capstone21.ui.me.MeFragmentDirections
import kotlinx.coroutines.Dispatchers.Main


class PostFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = PostFragmentDirections.actionNavigationPostToPermissionsFragment()
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



//        val action = PermissionsFragmentDirections.actionPermissionsToCamera()
//        findNavController().navigate(action)
    }
}