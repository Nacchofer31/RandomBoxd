package com.nacchofer31.randomboxd.core.data

import android.content.Context

actual class OnboardingPreferences(
    context: Context,
) {
    private val prefs = context.getSharedPreferences("randomboxd_prefs", Context.MODE_PRIVATE)

    actual fun isFirstRun(): Boolean = prefs.getBoolean(KEY_FIRST_RUN, true)

    actual fun setOnboardingCompleted() {
        prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()
    }

    companion object {
        private const val KEY_FIRST_RUN = "is_first_run"
    }
}
