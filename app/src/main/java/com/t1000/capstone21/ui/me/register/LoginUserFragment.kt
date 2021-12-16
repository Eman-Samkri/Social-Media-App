package com.t1000.capstone21.ui.me.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.R
import com.t1000.capstone21.camera.PermissionsFragmentDirections
import com.t1000.capstone21.databinding.FragmentLoginUserBinding
import com.t1000.capstone21.databinding.FragmentRegisterUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class LoginUserFragment : Fragment(R.layout.fragment_login_user) {


    private lateinit var binding: FragmentLoginUserBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginUserBinding.bind(view)


        binding.loginButtonLogin.setOnClickListener{

            loginUser()
        }


    }

    private fun loginUser(){
        val email = binding.emailEdittextLogin.text.toString()
        val password = binding.passwordEdittextLogin.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "sucssful Login", Toast.LENGTH_LONG).show()
                            }
                        }
                } catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }


}