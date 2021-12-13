package com.t1000.capstone21.ui.me.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.FragmentRegisterUserBinding
import com.t1000.capstone21.ui.me.MeViewModel

private const val TAG = "RegisterUserFragment"
class RegisterUserFragment : Fragment(){

    private lateinit var binding: FragmentRegisterUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: MeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_user, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterUserBinding.bind(view)


        binding.registerLoginBtn.setOnClickListener{
            registerUser()
        }


    }


    private fun registerUser(){

        if (binding.registerEmail.text.toString().isNotEmpty() &&
            binding.registerPassword.text.toString().isNotEmpty() &&
            binding.registerUserName.text.toString().isNotEmpty()) {
            auth.createUserWithEmailAndPassword(binding.registerEmail.text.toString(),
                binding.registerPassword.text.toString())
                .addOnCompleteListener { task->
                    if (task.isSuccessful){
                        Toast.makeText(context,"sucssful", Toast.LENGTH_LONG).show()
                    }else{
                        Log.e(TAG, "there was something wrong",task.exception)
                    }

                }
        }

    }


}