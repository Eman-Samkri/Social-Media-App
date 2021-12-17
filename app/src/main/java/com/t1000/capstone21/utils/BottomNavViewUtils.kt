package com.t1000.capstone21.utils

import android.app.Activity
import android.view.View
import com.t1000.capstone21.MainActivity

object BottomNavViewUtils {

    fun showBottomNavBar(activity: Activity?) = changeVisibility(activity, shouldShow = true)
    fun hideBottomNavBar(activity: Activity?) = changeVisibility(activity, shouldShow = false)

    private fun changeVisibility(activity: Activity?, shouldShow: Boolean) {
        val bottomNavigationView = (activity as? MainActivity)?.binding?.navView
        bottomNavigationView?.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

}