package com.nacchofer31.randomboxd.di

import com.nacchofer31.randomboxd.dependencies.DBClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::DBClient)
}