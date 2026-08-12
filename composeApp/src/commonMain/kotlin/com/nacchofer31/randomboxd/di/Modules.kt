package com.nacchofer31.randomboxd.di

import com.nacchofer31.randomboxd.core.data.DefaultDispatchers
import com.nacchofer31.randomboxd.core.data.RandomBoxdDatabase
import com.nacchofer31.randomboxd.core.data.RandomBoxdHttpClientFactory
import com.nacchofer31.randomboxd.core.domain.DispatcherProvider
import com.nacchofer31.randomboxd.history.data.repository_impl.FilmHistoryRepositoryImpl
import com.nacchofer31.randomboxd.history.domain.repository.FilmHistoryRepository
import com.nacchofer31.randomboxd.history.presentation.viewmodel.HistoryViewModel
import com.nacchofer31.randomboxd.random_film.data.repository_impl.RandomFilmScrappingRepository
import com.nacchofer31.randomboxd.random_film.data.repository_impl.UserNameRepositoryImpl
import com.nacchofer31.randomboxd.random_film.domain.repository.RandomFilmRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.UserNameRepository
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

expect val platformModule: Module

@OptIn(ExperimentalTime::class)
val sharedModule =
    module {
        single { RandomBoxdHttpClientFactory.create(get()) }
        singleOf(::RandomFilmScrappingRepository).bind<RandomFilmRepository>()
        singleOf(::DefaultDispatchers).bind<DispatcherProvider>()
        singleOf(::UserNameRepositoryImpl).bind<UserNameRepository>()
        single<Clock> { Clock.System }
        single { get<RandomBoxdDatabase>().filmHistoryDao() }
        single { FilmHistoryRepositoryImpl(get(), get()) } bind FilmHistoryRepository::class
        viewModelOf(::RandomFilmViewModel)
        viewModelOf(::HistoryViewModel)
    }
