package com.t1000.capstone21.ui.comment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.t1000.capstone21.databinding.CommentFragmentBinding
import com.t1000.capstone21.databinding.ItemVideoCommentBinding
import com.t1000.capstone21.models.Comment
import com.t1000.capstone21.models.Video


private const val TAG = "CommentFragment"
class CommentFragment : BottomSheetDialogFragment() {

    private lateinit var binding :CommentFragmentBinding

    private val args: CommentFragmentArgs by navArgs()
    private lateinit var auth: FirebaseAuth
    private lateinit var video:Video


    private val viewModel by lazy { ViewModelProvider(this).get(CommentViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = CommentFragmentBinding.inflate(layoutInflater)




        binding.addStickerBtn.setOnClickListener {
            val currentUser:String = args.currentUserId.toString()
            val currentVideo:String = args.currentVideoId.toString()
            val action = CommentFragmentDirections.actionCommentFragmentToStickerFragment(currentVideo,currentUser)
            findNavController().navigate(action)
        }


        binding.commentRv.layoutManager = LinearLayoutManager(context)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.currentVideoId?.let {
            viewModel.fetchVideosComment(it).observe(
                viewLifecycleOwner, Observer{
                    it?.let{
                        Log.e(TAG, "onViewCreated: list $it ")
                        it.forEach {
                            video =it
                            val comments =  it.comments
                            binding.commentRv.adapter = CommentAdapter(comments)
                            //   CommentAdapter().setData()
                        }

                    }

                })
        }


        binding.addCommentBtn.setOnClickListener {
           val  comment = binding.addNewCommentETV.text.toString()
            uploadComment(comment)
        }



    }



    private inner class CommentHolder(val binding:ItemVideoCommentBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(comment:Comment){
            if (comment.commentType == "Image"){
                Glide.with(this@CommentFragment).asGif().load(comment.commentText).into(binding.commentImg)
                binding.commentText.visibility = View.GONE

            }else if (comment.commentType == "Text"){
                binding.commentText.text = comment.commentText
                binding.commentImg.visibility = View.GONE
            }


            binding.userTv.text = comment.userId

            if (auth.currentUser?.uid != video.userId){
                binding.deletCommentBtn.visibility = View.GONE
                Log.e(TAG, "bind: cureeent user ${auth.currentUser?.uid}--- ${args.currentVideoId}", )
            }
            binding.deletCommentBtn.setOnClickListener {
                args.currentVideoId?.let { it1 -> viewModel.deleteVideoComment(it1,adapterPosition) }
                Log.e(TAG, "bind: deleted ${args.currentVideoId} ----$adapterPosition" )

            }


        }

    }

    private inner class CommentAdapter(val comments:List<Comment>):
        RecyclerView.Adapter<CommentHolder>() {

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

        fun setData(newCommentList:List<Comment>){
            val diffUtil = RvDiffUtil(comments,newCommentList)
            val diffResult = DiffUtil.calculateDiff(diffUtil)

            diffResult.dispatchUpdatesTo(this)
        }


    }

    private fun uploadComment(commentString: String) {
        val comment = Comment()
        comment.commentText = commentString
        comment.userId = args.currentUserId.toString()
        comment.videoId = args.currentVideoId.toString()
        comment.commentType = "Text"
        viewModel.saveCommentToFirestore(args.currentVideoId.toString(), comment)

        Log.e(TAG, "uploadComment Text: ${video}",)

    }
}