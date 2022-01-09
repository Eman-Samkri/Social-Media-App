package com.t1000.capstone21.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
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
import java.util.*

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
             senderId = FirebaseAuth.getInstance().currentUser?.uid!!

            reciverId = "eg8ZGcNTlHSirHMboKrV2HCcHkR2"

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
        Log.e(TAG, "onViewCreated1: sendr = $senderId, reciver =$reciverId", )

            //pass messages list for recycler to show
            viewModel.loadChatMessages(senderId,reciverId).observe(
                viewLifecycleOwner, androidx.lifecycle.Observer {
                    Log.e(TAG, "onViewCreated: sendr = $senderId, reciver =$reciverId", )
                   val  messageList = it as MutableList<ChatMessage>
                    binding.recycler.adapter = ChatAdapter(messageList)

                    //scroll to last items in recycler (recent messages)
//                    binding.recycler.scrollToPosition(it.size - 1)

                })


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
        val chatMessage = ChatMessage(text = binding.messageEditText.text.toString(), created_at = Timestamp(Date()))
        viewModel.sendMessage(senderId,args.chatReceivedId.toString(),chatMessage)

        binding.messageEditText.setText("")
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
                 val action = ChatFragmentDirections.actionNavigationIndexToFollowFragment(senderId,true)
           findNavController().navigate(action)
                true
            }

            else -> super.onOptionsItemSelected(item)


        }
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