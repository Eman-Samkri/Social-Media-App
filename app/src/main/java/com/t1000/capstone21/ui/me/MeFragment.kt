package com.t1000.capstone21.ui.me

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.FragmentMeBinding
import com.t1000.capstone21.databinding.FragmentRegisterUserBinding
import com.t1000.capstone21.ui.me.register.RegisterUserFragment


private const val TAG = "MeFragment"
class MeFragment : Fragment() {

    private lateinit var binding:FragmentMeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMeBinding.bind(view)

        binding.singUpBtn.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frameLayout2, RegisterUserFragment())
            transaction?.commit()
        }
    }

}