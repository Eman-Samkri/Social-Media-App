package com.t1000.capstone21.ui.chat

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.R

class IndexFragment:Fragment() {

    private lateinit var currentUserId :String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        if (FirebaseAuth.getInstance().currentUser?.uid == null){
            val action = ChatFragmentDirections.actionNavigationIndexToNavigationMe()
            findNavController().navigate(action)
        }else{
            currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!

        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_chat,menu)


        val chatItem =menu.findItem(R.id.newChatAction)
        val chatView = chatItem.actionView



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.newChatAction -> {
               // val action = ChatFragmentDirections.actionNavigationIndexToContactFragment(currentUserId,true)
                findNavController().navigate(R.id.contactFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)


        }
    }
}