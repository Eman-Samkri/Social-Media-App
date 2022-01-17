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

    private val RC_SIGN_IN: Int =1
    private lateinit var binding: FragmentLoginUserBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleCreateRequest()

        phoneCreateRequest()

    }

    private fun googleCreateRequest() {
        auth = FirebaseAuth.getInstance()
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.add_text))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun phoneCreateRequest() {
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phoneNumber)       // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(requireActivity())                 // Activity (for callback binding)
//            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
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

//        binding.loginButtonGoogle.setOnClickListener {
//            signInWithGoogle()
//        }

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

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
                Toast.makeText(context, "successfully register user $", Toast.LENGTH_LONG).show()

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (!task.isSuccessful) return@addOnCompleteListener
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val action = LoginUserFragmentDirections.actionLoginUserFragmentToProfileFragment(null)
                    findNavController().navigate(action)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

}