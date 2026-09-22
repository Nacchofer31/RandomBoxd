package com.nacchofer31.randomboxd.utils

import androidx.test.platform.app.InstrumentationRegistry
import com.nacchofer31.randomboxd.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin

/**
 * Starts the real Koin graph for instrumented tests.
 *
 * The application entry point that used to call `initKoin()` now lives in the `androidApp`
 * module, so library device tests have to boot Koin themselves before rendering the
 * `*Root` composables that rely on `koinViewModel`.
 */
fun startTestKoin() {
    if (GlobalContext.getOrNull() == null) {
        initKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext)
        }
    }
}

fun stopTestKoin() {
    if (GlobalContext.getOrNull() != null) {
        stopKoin()
    }
}
