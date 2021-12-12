package com.t1000.capstone21.utils

import android.app.Application
import com.t1000.capstone21.Repo

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Repo.initialize(this)
    }
}