package io.realm.curatedsyncexamples.fieldencryption.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.realm.curatedsyncexamples.fieldencryption.ui.dogs.DogsScreen
import io.realm.curatedsyncexamples.fieldencryption.ui.dogs.DogsViewModel
import io.realm.curatedsyncexamples.fieldencryption.ui.keystore.KeyStoreScreen
import kotlinx.coroutines.CoroutineScope

object Screens {
    const val LOGIN_SCREEN = "LOGIN_SCREEN"
    const val KEYSTORE_PASSWORD_SCREEN = "KEYSTORE_PASSWORD"
    const val DOGS_SCREEN = "DOGS_SCREEN"
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = Screens.LOGIN_SCREEN,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(
            Screens.LOGIN_SCREEN,
        ) {
            LoginScreen {
                navActions.navigateToKeyStoreUnlockScreen()
            }
        }
        composable(
            Screens.KEYSTORE_PASSWORD_SCREEN,
        ) {
            KeyStoreScreen {
                navActions.navigateToDogsScreen()
            }
        }
        composable(
            Screens.DOGS_SCREEN,
        ) {
            DogsScreen(DogsViewModel()) {
                navActions.navigateToLogin()
            }
        }
    }
}

class NavigationActions(private val navController: NavHostController) {
    fun navigateToKeyStoreUnlockScreen() {
        navController.navigate(Screens.KEYSTORE_PASSWORD_SCREEN) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    fun navigateToDogsScreen() {
        navController.navigate(Screens.DOGS_SCREEN) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
                inclusive = true
            }

            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    fun navigateToLogin() {
        navController.navigate(Screens.LOGIN_SCREEN) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}