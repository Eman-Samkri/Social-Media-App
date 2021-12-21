package com.t1000.capstone21.ui.me.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.t1000.capstone21.R
import com.t1000.capstone21.camera.PermissionsFragmentDirections
import com.t1000.capstone21.databinding.FragmentRegisterUserBinding
import com.t1000.capstone21.models.User


private const val TAG = "RegisterUserFragment"
class RegisterUserFragment : Fragment(){

    private lateinit var binding: FragmentRegisterUserBinding
    private lateinit var auth: FirebaseAuth
    private val userFirestore = Firebase.firestore

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

        binding.goToLoginBtn.setOnClickListener {
          Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
           PermissionsFragmentDirections.actionPermissionsToCamera())
        }
    }


    private fun registerUser(){


        val email = binding.registerEmail.text.toString()
        val password = binding.registerPassword.text.toString()
        val username = binding.registerUserName.text.toString()


        if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                    auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val currentUser = User(auth?.uid!!, username, email)
                                userFirestore.collection("users")
                                    .document(Firebase.auth?.uid!!)
                                    .set(currentUser)
                                Toast.makeText(context, "successfully register user $username", Toast.LENGTH_LONG).show()
                            }
                        }

            }
        }

    }



