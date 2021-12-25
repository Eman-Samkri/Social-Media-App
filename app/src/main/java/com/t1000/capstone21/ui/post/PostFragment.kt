package com.t1000.capstone21.ui.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController



class PostFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = PostFragmentDirections.actionNavigationPostToPermissionsFragment()
        findNavController().navigate(action)
    }


}