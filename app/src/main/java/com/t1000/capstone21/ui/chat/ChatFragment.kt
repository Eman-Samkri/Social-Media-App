package com.t1000.capstone21.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.ChatFragmentBinding
import com.t1000.capstone21.databinding.ItemVideoCommentBinding
import com.t1000.capstone21.models.ChatMessage
import com.t1000.capstone21.ui.comment.CommentFragmentDirections
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.suspendCoroutine

private const val TAG = "ChatFragment"
class ChatFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(ChatViewModel::class.java) }


    private lateinit var binding :ChatFragmentBinding

    private val args: ChatFragmentArgs by navArgs()

    private lateinit var senderId :String

    private lateinit var reciverId :String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        if (FirebaseAuth.getInstance().currentUser?.uid == null){
            val action = ChatFragmentDirections.actionNavigationIndexToNavigationMe()
            findNavController().navigate(action)
         }else{
             senderId = "L9B8qESSIwQ9gcBjIujGPH1s2Vx2"

            reciverId = FirebaseAuth.getInstance().currentUser?.uid!!

        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChatFragmentBinding.inflate(layoutInflater)

        binding.recycler.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.loadChatMessages(senderId,reciverId).observe( viewLifecycleOwner
            ) {
                Log.e(TAG, "onViewCreated: sendr = $senderId, reciver =$reciverId",)
                binding.recycler.adapter = ChatAdapter(it)
            }
        }



        //send message on keyboard done click
        binding.messageEditText.setOnEditorActionListener { _, actionId, _ ->
            sendMessage()
            true
        }
    }


    private fun sendMessage() {
        if (binding.messageEditText.text.isEmpty()) {
            Toast.makeText(context, "Empty String", Toast.LENGTH_LONG).show()
            return
        }
        val chatMessage = ChatMessage(senderId = senderId,receiverId = reciverId,
            text = binding.messageEditText.text.toString(), created_at = Timestamp(Date()))
        viewModel.sendMessage(senderId,args.chatReceivedId.toString(),chatMessage)

        binding.messageEditText.setText("")
    }




    private inner class ChatHolder(val binding: ItemVideoCommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(chatMessage: ChatMessage){
            binding.commentText.text = chatMessage.text
            binding.userTv.text = senderId
        }

    }


    private inner class ChatAdapter(val messages:List<ChatMessage>):
        RecyclerView.Adapter<ChatHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ):ChatHolder {
            val binding = ItemVideoCommentBinding.inflate(
                layoutInflater,
                parent,
                false
            )

            return ChatHolder(binding)

        }

        override fun onBindViewHolder(holder: ChatHolder, position: Int) {
            val chatItem: ChatMessage = messages[position]
            holder.bind(chatItem)
        }

        override fun getItemCount(): Int = messages.size


    }

}