package com.nacchofer31.randomboxd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nacchofer31.randomboxd.app.RandomBoxdApp
import com.nacchofer31.randomboxd.random_film.data.repository_impl.InAppReviewRepositoryImplAndroid
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val inAppReviewRepository: InAppReviewRepositoryImplAndroid by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        inAppReviewRepository.setActivity(this)
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
