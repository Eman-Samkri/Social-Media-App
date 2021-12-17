package com.t1000.capstone21.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.t1000.capstone21.CameraFragment
import com.t1000.capstone21.R


class PostFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_post, container, false)

//        activity?.let{
//            val intent = Intent (it, CameraActivity::class.java)
//            it.startActivity(intent)
//        }



        return v
    }


}