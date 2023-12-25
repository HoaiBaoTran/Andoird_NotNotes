package com.example.notnotes.appservice

import android.app.Application
import android.content.res.Configuration

class MyApplication : Application() {
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}