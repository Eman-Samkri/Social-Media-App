package com.t1000.capstone21.camera.preview

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.PreviewFragmentBinding

class PreviewFragment : Fragment() {

    private lateinit var binding:PreviewFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = PreviewFragmentBinding.inflate(layoutInflater)



        return binding.root
    }


}