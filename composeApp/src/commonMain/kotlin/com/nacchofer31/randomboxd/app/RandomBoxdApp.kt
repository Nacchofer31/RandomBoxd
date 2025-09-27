package com.nacchofer31.randomboxd.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdTypography
import com.nacchofer31.randomboxd.random_film.presentation.RandomFilmScreenRoot
import com.nacchofer31.randomboxd.random_film.presentation.viewmodel.RandomFilmViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
internal fun RandomBoxdApp() {
    MaterialTheme(
        typography = RandomBoxdTypography(),
    ) {
        val navController = rememberNavController()
        val localUriHandler = LocalUriHandler.current
        NavHost(
            navController = navController,
            startDestination = RandomBoxdRoute.Home,
        ) {
            navigation<RandomBoxdRoute.Home>(
                startDestination = RandomBoxdRoute.RandomFilm,
            ) {
                composable<RandomBoxdRoute.RandomFilm> {
                    val viewModel = koinViewModel<RandomFilmViewModel>()
                    RandomFilmScreenRoot(
                        viewModel = viewModel,
                        onFilmClicked = { film ->
                            localUriHandler.openUri(film.slug)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private inline fun <reified T : ViewModel> NavBackStackEntry.sharedKoinViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry =
        remember(this) {
            navController.getBackStackEntry(navGraphRoute)
        }
    return koinViewModel(
        viewModelStoreOwner = parentEntry,
    )
}
