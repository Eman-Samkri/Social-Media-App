package com.t1000.capstone21.ui.me

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.FragmentMeBinding
import com.t1000.capstone21.ui.profile.ProfileFragmentDirections


private const val TAG = "MeFragment"
class MeFragment : Fragment() {

    private lateinit var binding:FragmentMeBinding




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeBinding.inflate(layoutInflater)


        if (Firebase.auth.currentUser  != null){
            val action = MeFragmentDirections.actionNavigationMeToProfileFragment(FirebaseAuth.getInstance().currentUser?.uid)
            findNavController().navigate(action)
        }

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