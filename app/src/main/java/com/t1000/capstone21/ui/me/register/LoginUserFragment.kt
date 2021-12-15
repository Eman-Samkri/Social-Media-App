package com.t1000.capstone21.ui.me.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.FragmentLoginUserBinding
import com.t1000.capstone21.databinding.FragmentRegisterUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class LoginUserFragment : Fragment() {

//
//    private lateinit var binding: FragmentLoginUserBinding
//    private lateinit var auth: FirebaseAuth
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        auth = FirebaseAuth.getInstance()
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_login_user, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding = FragmentLoginUserBinding.bind(view)
//
//
//        binding.registerLoginBtn.setOnClickListener{
//            loginUser()
//        }
//
//
//    }
//
//    private fun loginUser(){
//        val email = binding.loginEmail.text.toString()
//        val password = binding.loginPassword.text.toString()
//        if (email.isNotEmpty() && password.isNotEmpty()) {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    auth.signInWithEmailAndPassword(email,password)
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                Toast.makeText(context, "sucssful Login", Toast.LENGTH_LONG).show()
//                            }
//                        }
//                } catch (e: Exception){
//                    withContext(Dispatchers.Main){
//                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//        }
//
//    }


}