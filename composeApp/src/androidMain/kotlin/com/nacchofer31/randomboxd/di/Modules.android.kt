package com.nacchofer31.randomboxd.di

import com.nacchofer31.randomboxd.core.data.OnboardingPreferences
import com.nacchofer31.randomboxd.core.data.RandomBoxdDatabase
import com.nacchofer31.randomboxd.database.getRandomBoxdDatabase
import com.nacchofer31.randomboxd.random_film.data.repository_impl.InAppReviewRepositoryImplAndroid
import com.nacchofer31.randomboxd.random_film.data.repository_impl.ShareRepositoryImplAndroid
import com.nacchofer31.randomboxd.random_film.domain.repository.InAppReviewRepository
import com.nacchofer31.randomboxd.random_film.domain.repository.ShareRepository
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module
    get() =
        module {
            single<HttpClientEngine> { OkHttp.create() }
            single<RandomBoxdDatabase> { getRandomBoxdDatabase(get()) }
            single { OnboardingPreferences(get()) }
            single { InAppReviewRepositoryImplAndroid() } bind InAppReviewRepository::class
            single { ShareRepositoryImplAndroid(get()) } bind ShareRepository::class
        }
