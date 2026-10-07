package com.ryzix.game2048

import android.app.Application

class EtcApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Sfx.init(this)
    }
}
