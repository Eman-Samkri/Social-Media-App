package com.t1000.capstone21.ui.chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.ChateFragmentBinding

class ChatFragment : Fragment() {

    private lateinit var binding :ChateFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChateFragmentBinding.inflate(layoutInflater)

        return binding.root
    }



}