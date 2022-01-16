package com.t1000.capstone21.ui.sticker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.t1000.capstone21.R
import com.t1000.capstone21.databinding.ItemStickerBinding
import com.t1000.capstone21.databinding.StickerFragmentBinding
import com.t1000.capstone21.giphy.model.Data
import com.t1000.capstone21.models.Comment


private const val TAG = "StickerFragment"
class StickerFragment : BottomSheetDialogFragment() {


    private val viewModel by lazy { ViewModelProvider(this).get(StickerViewModel::class.java) }


    private lateinit var binding :StickerFragmentBinding

    private val args: StickerFragmentArgs by navArgs()
    




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StickerFragmentBinding.inflate(layoutInflater)

        binding.stickerRv.layoutManager = GridLayoutManager(context,3)

          //  binding.stickerSearch



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dataLiveData.observe(
            viewLifecycleOwner, Observer {
                binding.shammer.visibility = View.GONE
                binding.stickerRv.visibility =View.VISIBLE
                binding.stickerRv.adapter = StickersAdapter(it)
                Log.e(TAG, "onViewCreated: $it")
            }
        )

    }


    private inner class StickersHolder(val binding: ItemStickerBinding)
        : RecyclerView.ViewHolder(binding.root),View.OnClickListener{

        private lateinit var data: Data

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(sticker: Data){
            data = sticker
            Glide.with(this@StickerFragment).asGif().load(sticker.images.downsized_still.url).into(binding.imageView3)

               // binding.stickerProgressBar.visibility = View.GONE
        }


        override fun onClick(v: View?) {
            if (v == itemView){
                uploadComment(data.images.downsized_still.url)
                val action = StickerFragmentDirections.actionStickerFragmentToCommentFragment(args.currentVideoId, args.currentUserId)
                findNavController().navigate(action)
            }
        }


    }


    private inner class StickersAdapter(val stickers:List<Data>):
        RecyclerView.Adapter<StickersHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickersHolder {
            val binding = ItemStickerBinding.inflate(
                layoutInflater,
                parent,
                false
            )
            return StickersHolder(binding)

        }

        override fun onBindViewHolder(holder: StickersHolder, position: Int) {
            val sticker = stickers[position]
            holder.bind(sticker)
        }

        override fun getItemCount(): Int  = stickers.size
    }
    private fun uploadComment(commentString: String) {
        val comment = Comment()
        comment.commentText = commentString
        comment.userId = args.currentUserId.toString()
        comment.videoId = args.currentVideoId.toString()
        comment.commentType = "Image"
        viewModel.saveCommentToFirestore(args.currentVideoId.toString(), comment)

        Log.e(TAG, "uploadComment: sticker is $comment", )

    }


}