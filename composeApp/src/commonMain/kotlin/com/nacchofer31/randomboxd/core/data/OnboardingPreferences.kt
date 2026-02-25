package com.nacchofer31.randomboxd.core.data

expect class OnboardingPreferences {
    fun isFirstRun(): Boolean

    fun setOnboardingCompleted()
}
