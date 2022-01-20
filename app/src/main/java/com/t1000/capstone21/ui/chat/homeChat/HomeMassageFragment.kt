package com.t1000.capstone21.ui.chat.homeChat

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.*
import com.t1000.capstone21.models.ChatParticipant
import com.t1000.capstone21.ui.chat.privateChat.ChatFragmentDirections

class HomeMassageFragment:Fragment() {

    private lateinit var currentUserId :String

    private lateinit var binding : HomeMassageFragmentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        Toast.makeText(context,"this feature not completed", Toast.LENGTH_LONG).show()


        if (FirebaseAuth.getInstance().currentUser?.uid == null){
            findNavController().navigate(R.id.loginUserFragment)
        }else{
            currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_chat,menu)

        menu.findItem(R.id.newChatAction)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.newChatAction -> {
                findNavController().navigate(R.id.contactFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeMassageFragmentBinding.inflate(layoutInflater)

        binding.homeMassageRv.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         binding.emptyAnim.visibility = View.VISIBLE


    }

    private inner class HomeChatHolder(val binding: ItemVideoCommentBinding): RecyclerView.ViewHolder(binding.root), View.OnClickListener{

       // private lateinit var userFollowId: String

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(chatParticipant: ChatParticipant){
         binding.userTv.text = chatParticipant.participant.username
         binding.userImg.load(chatParticipant.participant.profilePictureUrl)
         binding.commentText.text = chatParticipant.lastMessage
         binding.deletCommentBtn.visibility = View.GONE
        }

        override fun onClick(v: View?) {
            if (v == itemView){
            //    val action = FollowFragmentDirections.actionFollowFragmentToProfileFragment(userFollowId)
            //    findNavController().navigate(action)
            }
        }

    }

    private inner class HomeChatAdapter(val chatParticipant:List<ChatParticipant>):
        RecyclerView.Adapter<HomeChatHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeChatHolder {
            val binding = ItemVideoCommentBinding.inflate(
                layoutInflater,
                parent,
                false
            )

            return HomeChatHolder(binding)

        }

        override fun onBindViewHolder(holder: HomeChatHolder, position: Int) {
            val userItem : ChatParticipant = chatParticipant[position]
            holder.bind(userItem)
        }

        override fun getItemCount(): Int = chatParticipant.size

    }
}