package com.t1000.capstone21.ui.chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.databinding.ContactFragmentBinding
import com.t1000.capstone21.databinding.FollowFragmentBinding
import com.t1000.capstone21.databinding.ItemUserFollowBinding
import com.t1000.capstone21.models.User
import com.t1000.capstone21.ui.follow.FollowFragmentArgs

private const val TAG = "ContactFragment"

class ContactFragment : Fragment() {

    //private val args: ContactFragmentArgs by navArgs()

    val senderId = FirebaseAuth.getInstance().currentUser?.uid!!

    private val viewModel by lazy { ViewModelProvider(this).get(ContactViewModel::class.java) }

    private lateinit var binding : ContactFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ContactFragmentBinding.inflate(layoutInflater)

        binding.contacatRv.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchFollow(senderId).observe(
            viewLifecycleOwner, Observer{
                val followers = mutableListOf<User>()
                // must check come from followers or following

                    it.following.forEach { usersFollow ->
                        viewModel.fetchFollow(usersFollow).observe(
                            viewLifecycleOwner, Observer {follower->
                                followers += follower
                                binding.contacatRv.adapter = FollowAdapter(followers.distinct())
                            }
                        )
                    }

            })

    }


    private inner class FollowHolder(val binding: ItemUserFollowBinding):RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        private lateinit var chatReceivedId: String

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(user: User){
            chatReceivedId =user.userId
            binding.imageView.load(user.profilePictureUrl)
            binding.usernamTv.text = user.username
            Log.e(TAG, "bind: ${user.username}", )

        }

        override fun onClick(v: View?) {
            if (v == itemView){
                val action = ContactFragmentDirections.actionContactFragmentToNavigationIndex(chatReceivedId)
                findNavController().navigate(action)
            }
        }

    }

    private inner class FollowAdapter(val usersFollow:List<User>):
        RecyclerView.Adapter<FollowHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowHolder {
            val binding = ItemUserFollowBinding.inflate(
                layoutInflater,
                parent,
                false
            )

            return FollowHolder(binding)

        }

        override fun onBindViewHolder(holder: FollowHolder, position: Int) {
            val userItem : User = usersFollow[position]
            holder.bind(userItem)
        }

        override fun getItemCount(): Int = usersFollow.size


    }




}