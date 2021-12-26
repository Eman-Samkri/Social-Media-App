package com.t1000.capstone21.giphy.models

import com.google.gson.annotations.SerializedName


class Sticker (){
    val url:String = ""
    val id:String = ""
    @SerializedName("images")
    private lateinit var stickerLink :StickerLink
}
