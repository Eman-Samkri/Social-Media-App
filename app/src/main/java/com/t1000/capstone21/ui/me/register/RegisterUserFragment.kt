package com.t1000.capstone21.ui.me.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.FragmentRegisterUserBinding
import com.t1000.capstone21.ui.me.MeViewModel
import kotlinx.coroutines.*
import java.lang.Exception

private const val TAG = "RegisterUserFragment"
class RegisterUserFragment : Fragment(){

    private lateinit var binding: FragmentRegisterUserBinding
    private lateinit var auth: FirebaseAuth
    private val userFirestore = Firebase.firestore
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

        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        val username = binding.registerUserName.text.toString()


        if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                val currentUser = hashMapSet(user, username, email, password)
                                userFirestore.collection("users")
                                    .add(currentUser)
                                Toast.makeText(context, "sucssful", Toast.LENGTH_LONG).show()
                            }
                        }
                } catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }

    private fun hashMapSet(
        user: FirebaseUser?,
        username: String,
        email: String,
        password: String
    ): HashMap<String, String?> {
        val currentUser = hashMapOf(
            "User UID" to user?.uid,
            "username" to username,
            "email" to email,
            "password" to password
        )
        return currentUser
    }


}