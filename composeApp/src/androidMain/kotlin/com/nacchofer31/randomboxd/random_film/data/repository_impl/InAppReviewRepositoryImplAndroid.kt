package com.nacchofer31.randomboxd.random_film.data.repository_impl

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import com.nacchofer31.randomboxd.random_film.domain.repository.InAppReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class InAppReviewRepositoryImplAndroid : InAppReviewRepository {
    private var activityRef: WeakReference<Activity>? = null

    fun setActivity(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    override suspend fun requestInAppReview() {
        val activity = activityRef?.get() ?: return

        val prefs = activity.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hasShownReview = prefs.getBoolean("has_shown_review", false)

        if (!hasShownReview) {
            withContext(Dispatchers.Main) {
                val manager = ReviewManagerFactory.create(activity)
                val request = manager.requestReviewFlow()

                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        prefs.edit().putBoolean("has_shown_review", true).apply()
                        val reviewInfo = task.result
                        manager.launchReviewFlow(activity, reviewInfo)
                    }
                }
            }
        }
    }
}
