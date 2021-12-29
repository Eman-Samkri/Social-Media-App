package com.t1000.capstone21.ui.me.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.FragmentLoginUserBinding
import com.t1000.capstone21.databinding.FragmentRegisterUserBinding
import com.t1000.capstone21.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
private const val TAG = "LoginUserFragment"


class LoginUserFragment : Fragment() {

    private lateinit var binding: FragmentLoginUserBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginUserBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButtonLogin.setOnClickListener{
            loginUser()

        }

        binding.toRegisterTv.setOnClickListener {
            val action = LoginUserFragmentDirections.actionLoginUserFragmentToRegisterUserFragment()
            findNavController().navigate(action)
        }

    }

    private fun loginUser(){
        val email = binding.emailEdittextLogin.text.toString()
        val password = binding.passwordEdittextLogin.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

//        back_to_register_textview.visibility = View.GONE
//        loading_view.visibility = View.VISIBLE

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                if (it.isSuccessful){
                    Log.d(TAG, "Successfully logged in: ${it.result!!.user?.uid}")
                    val action = LoginUserFragmentDirections.actionLoginUserFragmentToProfileFragment(null)
                    findNavController().navigate(action)
                }


            }
            .addOnFailureListener {
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()

//                back_to_register_textview.visibility = View.VISIBLE
//                loading_view.visibility = View.GONE
            }
    }


}