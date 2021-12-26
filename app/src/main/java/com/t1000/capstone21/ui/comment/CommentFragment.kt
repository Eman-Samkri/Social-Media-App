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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.t1000.capstone21.databinding.CommentFragmentBinding
import com.t1000.capstone21.databinding.ItemVideoCommentBinding
import com.t1000.capstone21.models.Comment
import com.t1000.capstone21.models.Video


private const val TAG = "CommentFragment"
class CommentFragment : BottomSheetDialogFragment() {

    private lateinit var binding :CommentFragmentBinding

    private val args: CommentFragmentArgs by navArgs()


    private val viewModel by lazy { ViewModelProvider(this).get(CommentViewModel::class.java) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = CommentFragmentBinding.inflate(layoutInflater)




        binding.addStickerBtn.setOnClickListener {
            val action = CommentFragmentDirections.actionCommentFragmentToStickerFragment()
            findNavController().navigate(action)
        }


        binding.commentRv.layoutManager = LinearLayoutManager(context)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchVideosComment(args.currentVideoId).observe(
            viewLifecycleOwner, Observer{
                it?.let{
                    Log.e(TAG, "onViewCreated: list $it ")
                    it.forEach {
                       val comments =  it.comments
                            binding.commentRv.adapter = CommentAdapter(comments)
                        //    CommentAdapter().setData()
                    }

            }

            })

        binding.addCommentBtn.setOnClickListener {
           val  comment = binding.addNewCommentETV.text.toString()
            uploadComment(comment)
        }



    }



    private inner class CommentHolder(val binding:ItemVideoCommentBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(comment:Comment){

            binding.commentText.text = comment.commentText

            binding.deletCommentBtn.setOnClickListener {
                viewModel.deleteVideoComment(args.currentVideoId,adapterPosition)
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
        val video = Video(videoId = args.currentVideoId)
        comment.commentText = commentString
        comment.userId = video.userId
        comment.videoId = video.videoId
        viewModel.saveCommentToFirestore(video, comment)

        Log.e(TAG, "uploadComment: ${video}",)

    }
}