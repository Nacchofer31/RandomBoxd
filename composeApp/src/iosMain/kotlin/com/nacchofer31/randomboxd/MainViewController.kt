package com.nacchofer31.randomboxd

import androidx.compose.ui.window.ComposeUIViewController
import com.nacchofer31.randomboxd.app.RandomBoxdApp
import com.nacchofer31.randomboxd.di.initKoin

fun mainViewController() =
    ComposeUIViewController(
        configure = {
            initKoin()
        },
    ) { RandomBoxdApp() }
