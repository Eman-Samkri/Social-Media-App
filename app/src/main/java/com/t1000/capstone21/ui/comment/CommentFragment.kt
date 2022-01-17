package com.t1000.capstone21.ui.comment

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.AddCommentChatFragmentBinding
import com.t1000.capstone21.databinding.ItemVideoCommentBinding
import com.t1000.capstone21.giphy.model.Onclick
import com.t1000.capstone21.models.Comment
import com.t1000.capstone21.models.Video
import com.t1000.capstone21.ui.chat.contact.ContactFragmentDirections
import kotlinx.coroutines.launch


private const val TAG = "CommentFragment"
class CommentFragment : BottomSheetDialogFragment() {

    private lateinit var binding :AddCommentChatFragmentBinding


    private val args: CommentFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth


    private val viewModel by lazy { ViewModelProvider(this).get(CommentViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        if (FirebaseAuth.getInstance().currentUser?.uid == null){
            findNavController().navigate(R.id.navigation_me)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = AddCommentChatFragmentBinding.inflate(layoutInflater)

        binding.addStickerBtn.setOnClickListener {
            val currentUser:String = args.currentUserId.toString()
            val currentVideo:String = args.currentVideoId.toString()
            val action = CommentFragmentDirections.actionCommentFragmentToStickerFragment(currentVideo,currentUser)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.fetchVideosComment(args.currentVideoId.toString()).observe( viewLifecycleOwner
            ) {
                binding.recyclerView.adapter = CommentAdapter(it)
                binding.recyclerView.scrollToPosition(it.size-1)
                binding.recyclerView.visibility = View.VISIBLE

            }
        }





        //send comment on keyboard done click
        binding.addTextETV.setOnEditorActionListener { _, actionId, _ ->
            val  comment = binding.addTextETV.text.toString()
            uploadComment(comment)
            binding.addTextETV.setText("")
            true
        }



    }



    private inner class CommentHolder(val binding:ItemVideoCommentBinding):RecyclerView.ViewHolder(binding.root)
        , View.OnClickListener {

        private lateinit var userId: String

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(comment:Comment){
            userId = comment.userId
            val user = viewModel.fetchUserById(comment.userId)
            user.observe(
                viewLifecycleOwner, Observer{
                    it.forEach {
                        binding.shammer.visibility = View.GONE
                        binding.userTv.text = it.username
                        binding.userImg.load(it.profilePictureUrl)

                    }
                })


            if (comment.commentType == "Image"){
                Glide.with(this@CommentFragment).asGif().load(comment.commentText).into(binding.commentImg)
                binding.commentText.visibility = View.GONE

            }else if (comment.commentType == "Text"){
                binding.commentText.text = comment.commentText
                binding.commentImg.visibility = View.GONE
            }


            if (comment.userId.equals(auth.currentUser?.uid) ){
                binding.deletCommentBtn.visibility = View.VISIBLE
                Log.e(TAG, "bind: current user ${auth.currentUser?.uid}--- ${args.currentVideoId}", )
            }
            binding.deletCommentBtn.setOnClickListener {
                args.currentVideoId?.let { viewModel.deleteVideoComment(it,adapterPosition) }
                Log.e(TAG, "bind: deleted ${args.currentVideoId} ----$adapterPosition" )

            }

        }

        override fun onClick(v: View?) {
            if (v ==  itemView){
                val action = CommentFragmentDirections.actionCommentFragmentToProfileFragment(userId)
                findNavController().navigate(action)
            }
        }

    }

    private inner class CommentAdapter(val comments:List<Comment>):
        RecyclerView.Adapter<CommentHolder>(){

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CommentFragment.CommentHolder {
            val binding = ItemVideoCommentBinding.inflate(
                layoutInflater,
                parent,
                false
            )

            return CommentHolder(binding)
        }

        override fun onBindViewHolder(holder: CommentFragment.CommentHolder, position: Int) {
            val commentItem: Comment = comments[position]
            holder.bind(commentItem)
        }

        override fun getItemCount(): Int = comments.size


    }


    private fun uploadComment(commentString: String) {
        val comment = Comment()
        comment.commentText = commentString
        comment.userId = FirebaseAuth.getInstance().currentUser?.uid!!
        comment.videoId = args.currentVideoId.toString()
        comment.commentType = "Text"
        viewModel.saveCommentToFirestore(args.currentVideoId.toString(), comment)


    }
}