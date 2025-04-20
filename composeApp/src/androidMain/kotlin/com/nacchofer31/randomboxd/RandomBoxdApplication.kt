package com.nacchofer31.randomboxd

import android.app.Application
import com.nacchofer31.randomboxd.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class RandomBoxdApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@RandomBoxdApplication)
        }
    }
}
