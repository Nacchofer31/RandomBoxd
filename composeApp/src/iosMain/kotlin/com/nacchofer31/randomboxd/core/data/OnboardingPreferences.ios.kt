package com.nacchofer31.randomboxd.core.data

import platform.Foundation.NSUserDefaults

actual class OnboardingPreferences {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun isFirstRun(): Boolean = !defaults.boolForKey(KEY_ONBOARDING_COMPLETED)

    actual fun setOnboardingCompleted() {
        defaults.setBool(true, forKey = KEY_ONBOARDING_COMPLETED)
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}
