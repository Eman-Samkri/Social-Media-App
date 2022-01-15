package com.t1000.capstone21.camera.musicFragment

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
import coil.load
import com.t1000.capstone21.databinding.ItemMusicBinding
import com.t1000.capstone21.databinding.ItemStickerBinding
import com.t1000.capstone21.databinding.MusicFragmentBinding
import com.t1000.capstone21.music.model.Track

private const val TAG = "MusicFragment"
class MusicFragment : Fragment() {

    private lateinit var binding :MusicFragmentBinding

    private val viewModel by lazy { ViewModelProvider(this).get(MusicViewModel::class.java) }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MusicFragmentBinding.inflate(layoutInflater)

        binding.musicRv.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e(TAG, "onViewCreated: heeeer", )
        viewModel.dataLiveData.observe(
            viewLifecycleOwner, Observer {
                Log.e(TAG, "onViewCreated: $it", )
                binding.musicRv.adapter = MusicAdapter(it)
                Log.e(TAG, "onViewCreated: $it")
            }
        )

    }


    private inner class MusicHolder(val binding: ItemMusicBinding)
        : RecyclerView.ViewHolder(binding.root),View.OnClickListener{

//        private lateinit var data: Data
//
//        init {
//            itemView.setOnClickListener(this)
//        }

        fun bind(music: Track){
         //  binding.musicImg.load(music.image.forEach {  })

            binding.musicName.text = music.url


        }


        override fun onClick(v: View?) {
            if (v == itemView){

            }
        }


    }


    private inner class MusicAdapter(val music:List<Track>):
        RecyclerView.Adapter<MusicHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
            val binding = ItemMusicBinding.inflate(
                layoutInflater,
                parent,
                false
            )
            return MusicHolder(binding)

        }

        override fun onBindViewHolder(holder: MusicHolder, position: Int) {
            val musi = music[position]
            holder.bind(musi)
        }

        override fun getItemCount(): Int  = music.size
    }


}