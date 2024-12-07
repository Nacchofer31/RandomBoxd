package com.nacchofer31.randomboxd

import androidx.compose.ui.window.ComposeUIViewController
import com.nacchofer31.randomboxd.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }