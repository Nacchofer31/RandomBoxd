package com.nacchofer31.randomboxd

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.play.core.review.ReviewManagerFactory
import com.nacchofer31.randomboxd.app.RandomBoxdApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            RandomBoxdApp()
        }
        requestReviewIfNotShown()
    }

    private fun requestReviewIfNotShown() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasShownReview = prefs.getBoolean("has_shown_review", false)
        
        if (!hasShownReview) {
            val manager = ReviewManagerFactory.create(this)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    prefs.edit().putBoolean("has_shown_review", true).apply()
                    val reviewInfo = task.result
                    manager.launchReviewFlow(this, reviewInfo)
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    RandomBoxdApp()
}
