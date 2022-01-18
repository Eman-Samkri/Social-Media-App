package com.t1000.capstone21.ui.me.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.FragmentLoginUserBinding
import com.t1000.capstone21.utils.hideKeyboard

private const val TAG = "LoginUserFragment"


class LoginUserFragment : Fragment() {

    private lateinit var binding: FragmentLoginUserBinding








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
        val email = binding.loginEmail.text.toString()
        val password = binding.loginPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }


        hideKeyboard()
        binding.loading.visibility = View.VISIBLE
        binding.toRegisterTv.visibility = View.GONE

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                if (it.isSuccessful){
                    Log.d(TAG, "Successfully logged in: ${it.result!!.user?.uid}")
                    hideKeyboard()
                    val action = LoginUserFragmentDirections.actionLoginUserFragmentToProfileFragment(null)
                    findNavController().navigate(action)
                }


            }
            .addOnFailureListener {
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                binding.loading.visibility = View.GONE
            }


    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

}