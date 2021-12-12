package com.t1000.capstone21.ui.post

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.t1000.capstone21.CameraActivity
import com.t1000.capstone21.R
import kotlinx.coroutines.Dispatchers.Main


class PostFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_post, container, false)

        activity?.let{
            val intent = Intent (it,CameraActivity::class.java)
            it.startActivity(intent)
        }

        return v
    }
}