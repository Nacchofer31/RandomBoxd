package com.nacchofer31.randomboxd.di

import com.nacchofer31.randomboxd.dependencies.MyRepository
import com.nacchofer31.randomboxd.dependencies.MyRepositoryImpl
import com.nacchofer31.randomboxd.dependencies.MyViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    singleOf(::MyRepositoryImpl).bind<MyRepository>()
    viewModelOf(::MyViewModel)
}