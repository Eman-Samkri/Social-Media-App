package com.t1000.capstone21.camera.musicFragment

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.t1000.capstone21.databinding.ItemMusicBinding
import com.t1000.capstone21.databinding.MusicFragmentBinding
import com.t1000.capstone21.music.model.Track
import java.io.IOException


private const val TAG = "MusicFragment"
class MusicFragment : Fragment() {

    private lateinit var binding :MusicFragmentBinding

    private val viewModel by lazy { ViewModelProvider(this).get(MusicViewModel::class.java) }
    var mediaPlayer = MediaPlayer()



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

        Log.e(TAG, "onViewCreated: heeeer")
        viewModel.dataLiveData.observe(
            viewLifecycleOwner, Observer {
                Log.e(TAG, "onViewCreated: $it")
                binding.musicRv.adapter = MusicAdapter(it)
                Log.e(TAG, "onViewCreated: $it")
            }
        )

    }


    private inner class MusicHolder(val binding: ItemMusicBinding)
        : RecyclerView.ViewHolder(binding.root),View.OnClickListener{

        private lateinit var data: Track

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(music: Track){
         //  binding.musicImg.load(music.image.forEach {  })
            data = music
            binding.musicName.text = music.name


        }


        override fun onClick(v: View?) {
            if (v == itemView){
                playAudio(data.url)
                 Log.e(TAG, "onClick: data ${data.url}", )

            }
        }
        private fun playAudio(audioUrl:String) {

            // initializing media player

            // below line is use to set the audio
            // stream type for our media player.
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

            // below line is use to set our
            // url to our media player.
            try {
                mediaPlayer.setDataSource(audioUrl)
                // below line is use to prepare
                // and start our media player.
                mediaPlayer.prepare()
                mediaPlayer.start()
                Log.e(TAG, "playAudio: start", )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            // below line is use to display a toast message.
          //  Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show()
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