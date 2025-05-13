package com.nacchofer31.randomboxd.di

import com.nacchofer31.randomboxd.core.data.UsernameDatabase
import com.nacchofer31.randomboxd.database.getUserNameDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() =
        module {
            single<HttpClientEngine> { OkHttp.create() }
            single<UsernameDatabase> { getUserNameDatabase(get()) }
        }
