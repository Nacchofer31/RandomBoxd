package com.nacchofer31.randomboxd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nacchofer31.randomboxd.app.RandomBoxdApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            RandomBoxdApp()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    RandomBoxdApp()
}