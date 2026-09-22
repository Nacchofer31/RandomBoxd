# R8 rules for RandomBoxd (composeApp)
#
# The default proguard-android-optimize.txt handles Android components,
# Parcelable, enums and kotlinx-coroutines via bundled consumer rules.
# Room 2.7.2, kotlinx-serialization 1.9.0 and kotlin-reflect ship their own
# consumer rules. Koin 4.x resolves definitions by direct class reference
# (::singleOf/viewModelOf/koinViewModel<T>()), which R8 retains automatically,
# so no blanket rules are needed for it.
#
# Compose Multiplatform resources are accessed statically (Res.string.xxx) and
# resolved by asset path at runtime, so R8 retains the referenced members.

