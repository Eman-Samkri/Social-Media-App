package com.t1000.capstone21.stipop.models

import com.google.gson.annotations.SerializedName


class StickerResponse {
    @SerializedName("data")
    lateinit var Stickers:List<Sticker>
}