package com.t1000.capstone21.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.ChatFragmentBinding
import com.t1000.capstone21.models.ChatMessage
import com.t1000.capstone21.ui.comment.CommentViewModel
import java.util.*

class ChatFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(ChatViewModel::class.java) }


    private lateinit var binding :ChatFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChatFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //pass messages list for recycler to show
//        viewModel.loadChatMessages().observe(this, Observer { mMessagesList ->
//
//            //scroll to last items in recycler (recent messages)
//            binding.recycler.scrollToPosition(mMessagesList.size - 1)
//
//        })
    }


    private fun sendMessage(message: ChatMessage, senderId:String, receiverId:String) {
        if (binding.messageEditText.text.isEmpty()) {
            Toast.makeText(context, "Empty String", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.sendMessage(message,senderId,receiverId)

        binding.messageEditText.setText("")
    }



}