package com.t1000.capstone21.ui.comment

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
import com.t1000.capstone21.databinding.CommentFragmentBinding
import com.t1000.capstone21.databinding.ItemVideoCommentBinding
import com.t1000.capstone21.models.Comment
import com.t1000.capstone21.models.Video
import com.t1000.capstone21.ui.me.register.LoginUserFragmentDirections

private const val TAG = "CommentFragment"
class CommentFragment : Fragment() {

    private lateinit var binding :CommentFragmentBinding

    private val args: CommentFragmentArgs by navArgs()

    private val viewModel by lazy { ViewModelProvider(this).get(CommentViewModel::class.java) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = CommentFragmentBinding.inflate(layoutInflater)


        binding.addCommentBtn.setOnClickListener {
            val comment = binding.addNewCommentETV.text.toString()
            uploadComment(comment)
        }

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

                    }

            }

            })



    }



    private inner class CommentHolder(val binding:ItemVideoCommentBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(comment:Comment){

            binding.commentText.text = comment.commentText
            binding.deletCommentBtn.setOnClickListener {

            }
            //adapterPosition
           // binding.imageView2


        }

    }

    private inner class CommentAdapter(val comments:List<Comment>):
        RecyclerView.Adapter<CommentHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentFragment.CommentHolder {
            val binding = ItemVideoCommentBinding.inflate(
                layoutInflater,
                parent,
                false
            )

            return CommentHolder(binding)

        }

        override fun onBindViewHolder(holder: CommentFragment.CommentHolder, position: Int) {
            val commentItem : Comment = comments[position]
            holder.bind(commentItem)
        }

        override fun getItemCount(): Int = comments.size


    }

    private fun uploadComment(commentString:String) {
        val comment = Comment()
        val video = Video(videoId = args.currentVideoId)
        comment.commentText = commentString
        comment.userId = video.userId
        comment.videoId = video.videoId
        viewModel.saveCommentToFirestore(video,comment)
        
        Log.e(TAG, "uploadComment: ${video}", )

    }


}