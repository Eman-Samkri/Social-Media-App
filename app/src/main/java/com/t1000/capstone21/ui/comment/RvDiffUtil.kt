package com.t1000.capstone21.ui.comment

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.t1000.capstone21.models.Comment

class RvDiffUtil(
    private val oldCommentList:List<Comment>,
    private val newCommentList:List<Comment>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
       return oldCommentList.size
    }

    override fun getNewListSize(): Int {
        return newListSize
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCommentList[oldItemPosition].videoId == newCommentList[newItemPosition].videoId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        when{
            oldCommentList[oldItemPosition].commentText != newCommentList[newItemPosition].commentText ->{
                return false
            }

            oldCommentList[oldItemPosition].videoId != newCommentList[newItemPosition].videoId ->{
                return false
            }

            oldCommentList[oldItemPosition].commentLikes != newCommentList[newItemPosition].commentLikes ->{
                return false
            }

            else -> return true

        }

    }

}