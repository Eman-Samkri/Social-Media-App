package com.t1000.capstone21.ui.me.register

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.databinding.FragmentRegisterUserBinding

private const val TAG = "RegisterUserFragment"
class RegisterUserFragment : Fragment(){

//        private lateinit var binding: FragmentRegisterUserBinding
//        private lateinit var auth: FirebaseAuth
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding = FragmentRegisterUserBinding.bind(view)
//
//
//        binding.registerLoginBtn.setOnClickListener{
//            registerUser()
//        }
//
//
//    }
//
//
//    private fun registerUser(){
//
//        if (binding.registerEmail.toString().isNotEmpty() &&
//            binding.registerPassword.toString().isNotEmpty() &&
//            binding.registerUserName.toString().isNotEmpty()) {
//            auth.createUserWithEmailAndPassword(binding.registerEmail.toString(),binding.registerPassword.toString())
//                .addOnCompleteListener { task->
//                    if (task.isSuccessful){
//                        Toast.makeText(context,"sucssful", Toast.LENGTH_LONG).show()
//                    }else{
//                        Log.e(TAG , "there was something wrong",task.exception)
//                    }
//
//                }
//        }
//
//    }

}