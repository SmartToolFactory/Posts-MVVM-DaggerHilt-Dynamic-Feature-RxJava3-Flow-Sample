package com.smarttoolfactory.postdynamichilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PostApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
