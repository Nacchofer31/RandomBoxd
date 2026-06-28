package com.nacchofer31.randomboxd.random_film.data.repository_impl

import com.nacchofer31.randomboxd.random_film.domain.repository.InAppReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.StoreKit.SKStoreReviewController
import platform.UIKit.UIApplication

class InAppReviewRepositoryImplIos : InAppReviewRepository {
    override suspend fun requestInAppReview() {
        withContext(Dispatchers.Main) {
            val windowScene = UIApplication.sharedApplication.connectedScenes.anyObject() as? platform.UIKit.UIWindowScene
            if (windowScene != null) {
                SKStoreReviewController.requestReviewInScene(windowScene)
            } else {
                SKStoreReviewController.requestReview()
            }
        }
    }
}
