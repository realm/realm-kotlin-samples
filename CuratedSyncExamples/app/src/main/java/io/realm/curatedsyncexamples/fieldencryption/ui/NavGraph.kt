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
import io.realm.curatedsyncexamples.fieldencryption.FIELD_LEVEL_ENCRYPTION_KEY_ALIAS
import io.realm.curatedsyncexamples.fieldencryption.models.AndroidKeyStoreHelper
import io.realm.curatedsyncexamples.fieldencryption.ui.records.SecretRecordScreen
import io.realm.curatedsyncexamples.fieldencryption.ui.records.SecretRecordsViewModel
import io.realm.curatedsyncexamples.fieldencryption.ui.keystore.KeyStoreScreen
import io.realm.curatedsyncexamples.fieldencryption.ui.keystore.KeyStoreViewModel
import io.realm.curatedsyncexamples.fieldencryption.ui.login.LoginScreen
import io.realm.curatedsyncexamples.fieldencryption.ui.login.LoginViewModel
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope

object Screens {
    const val LOGIN_SCREEN = "LOGIN_SCREEN"
    const val KEYSTORE_PASSWORD_SCREEN = "KEYSTORE_PASSWORD"
    const val SECRET_RECORDS_SCREEN = "SECRET_RECORDS_SCREEN"
}

@Composable
fun NavGraph(
    app: App,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String =
        when {
            app.currentUser == null -> Screens.LOGIN_SCREEN
            !AndroidKeyStoreHelper.containsKey(FIELD_LEVEL_ENCRYPTION_KEY_ALIAS) -> Screens.KEYSTORE_PASSWORD_SCREEN
            else -> Screens.SECRET_RECORDS_SCREEN
        },
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
            LoginScreen(
                LoginViewModel(app)
            ) {
                navActions.navigateToKeyStoreUnlockScreen()
            }
        }
        composable(
            Screens.KEYSTORE_PASSWORD_SCREEN,
        ) {
            KeyStoreScreen(KeyStoreViewModel(app)) {
                navActions.navigateToSecretRecordsScreen()
            }
        }
        composable(
            Screens.SECRET_RECORDS_SCREEN,
        ) {
            SecretRecordScreen(SecretRecordsViewModel(app)) {
                navActions.navigateToLogin()
            }
        }
    }
}

class NavigationActions(private val navController: NavHostController) {
    fun navigateToKeyStoreUnlockScreen() {
        navController.navigate(Screens.KEYSTORE_PASSWORD_SCREEN) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToSecretRecordsScreen() {
        navController.navigate(Screens.SECRET_RECORDS_SCREEN) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToLogin() {
        navController.navigate(Screens.LOGIN_SCREEN) {
            popUpTo(Screens.SECRET_RECORDS_SCREEN) {
                inclusive = true
                saveState = true
            }
            launchSingleTop = true
        }
    }
}