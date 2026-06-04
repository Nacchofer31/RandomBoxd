package com.nacchofer31.randomboxd

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import com.nacchofer31.randomboxd.appfunctions.RandomBoxdAppFunctions
import com.nacchofer31.randomboxd.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class RandomBoxdApplication :
    Application(),
    AppFunctionConfiguration.Provider {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@RandomBoxdApplication)
        }
    }

    override val appFunctionConfiguration: AppFunctionConfiguration
        get() =
            AppFunctionConfiguration
                .Builder()
                .addEnclosingClassFactory(RandomBoxdAppFunctions::class.java) {
                    GlobalContext.get().get<RandomBoxdAppFunctions>()
                }.build()
}
