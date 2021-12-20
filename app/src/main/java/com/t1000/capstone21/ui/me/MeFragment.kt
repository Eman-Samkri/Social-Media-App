package com.t1000.capstone21.ui.me

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.FragmentMeBinding



private const val TAG = "MeFragment"
class MeFragment : Fragment() {

    private lateinit var binding:FragmentMeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.singUpBtn.setOnClickListener {
            val action = MeFragmentDirections.actionNavigationMeToLoginUserFragment()
            findNavController().navigate(action)
        }
    }

}