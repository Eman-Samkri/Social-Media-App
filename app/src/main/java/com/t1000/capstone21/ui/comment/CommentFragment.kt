package com.t1000.capstone21.ui.comment

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.t1000.capstone21.R
import com.t1000.capstone21.camera.videoFragment.VideoFragmentVM
import com.t1000.capstone21.databinding.CommentFragmentBinding
import com.t1000.capstone21.databinding.ItemHomeVideoBinding
import com.t1000.capstone21.databinding.ItemVideoCommentBinding
import com.t1000.capstone21.models.Comment
import com.t1000.capstone21.models.Video
import com.t1000.capstone21.ui.home.HomeFragment

class CommentFragment : Fragment() {

    private lateinit var binding :CommentFragmentBinding

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

        binding.commentRv.layoutManager = LinearLayoutManager(context)

//        val list :List<Comment> = listOf()
//
//        binding.commentRv.adapter = CommentAdapter(list)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchVideosComment().observe(
            viewLifecycleOwner, Observer{
              //  Log.e(TAG, "onViewCreated: list $it ")

                binding.commentRv.adapter = CommentAdapter(it)

            })

    }



    private inner class CommentHolder(val binding:ItemVideoCommentBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(comment:Comment){

            binding.commentText.text = comment.commentText
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
        viewModel.saveCommentToFirestore(comment,commentString)

    }


}