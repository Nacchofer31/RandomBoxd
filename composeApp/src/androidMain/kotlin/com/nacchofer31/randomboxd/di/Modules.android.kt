package com.nacchofer31.randomboxd.di

import com.nacchofer31.randomboxd.core.data.OnboardingPreferences
import com.nacchofer31.randomboxd.core.data.UsernameDatabase
import com.nacchofer31.randomboxd.database.getUserNameDatabase
import com.nacchofer31.randomboxd.random_film.data.repository_impl.InAppReviewRepositoryImplAndroid
import com.nacchofer31.randomboxd.random_film.domain.repository.InAppReviewRepository
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module
    get() =
        module {
            single<HttpClientEngine> { OkHttp.create() }
            single<UsernameDatabase> { getUserNameDatabase(get()) }
            single { OnboardingPreferences(get()) }
            single { InAppReviewRepositoryImplAndroid() } bind InAppReviewRepository::class
        }
