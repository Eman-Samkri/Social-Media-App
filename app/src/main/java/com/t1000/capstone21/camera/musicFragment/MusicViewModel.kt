package com.t1000.capstone21.camera.musicFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.t1000.capstone21.Repo
import com.t1000.capstone21.giphy.repo.GiphyRepo
import com.t1000.capstone21.music.model.Track
import com.t1000.capstone21.music.repo.LastFMRepo

class MusicViewModel : ViewModel() {


    private val apiMusic: LastFMRepo = LastFMRepo()


    val dataLiveData: LiveData<List<Track>> = apiMusic.getMusic()}