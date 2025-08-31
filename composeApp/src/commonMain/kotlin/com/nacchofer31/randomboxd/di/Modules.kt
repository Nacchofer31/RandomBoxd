package com.nacchofer31.randomboxd.di

import com.nacchofer31.randomboxd.core.data.DefaultDispatchers
import com.nacchofer31.randomboxd.core.data.RandomBoxdHttpClientFactory
import com.nacchofer31.randomboxd.core.domain.DispatcherProvider
import com.nacchofer31.randomboxd.random_film.data.repository_impl.RandomFilmRepositoryImpl
import com.nacchofer31.randomboxd.random_film.data.repository_impl.UserNameRepositoryImpl
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.UserNameRepository
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule =
    module {
        single { RandomBoxdHttpClientFactory.create(get()) }
        singleOf(::RandomFilmRepositoryImpl).bind<RandomFilmRepository>()
        singleOf(::DefaultDispatchers).bind<DispatcherProvider>()
        singleOf(::UserNameRepositoryImpl).bind<UserNameRepository>()
        viewModelOf(::RandomFilmViewModel)
    }
