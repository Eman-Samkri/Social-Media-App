package com.t1000.capstone21.ui.sticker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.t1000.capstone21.databinding.ItemStickerBinding
import com.t1000.capstone21.databinding.StickerFragmentBinding
import com.t1000.capstone21.giphy.model.Data


private const val TAG = "StickerFragment"
class StickerFragment : BottomSheetDialogFragment() {


    private val viewModel by lazy { ViewModelProvider(this).get(StickerViewModel::class.java) }


    private lateinit var binding :StickerFragmentBinding



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

        viewModel.stickersLiveData().observe(
            viewLifecycleOwner, {
                binding.stickerRv.adapter = StickersAdapter(it)
                Log.e(TAG, "onViewCreated: $it", )
            }
        )

    }


    private inner class StickersHolder(val binding: ItemStickerBinding)
        : RecyclerView.ViewHolder(binding.root),View.OnClickListener{

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(sticker: Data){
            binding.StickerVideoView.setVideoPath(sticker.images.downsized_small.mp4)
                binding.stickerProgressBar.visibility = View.GONE
                binding.StickerVideoView.start()

        }

        override fun onClick(v: View?) {
//            val video = Video(args.currentVideoId)
//            val comment = Comment(args.currentCommentId)
//            comment.commentText =
//
//                sticker.images.downsized_small.mp4
//
//          viewModel.saveCommentToFirestore()

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



}