package io.realm.curatedsyncexamples.fieldencryption.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.realm.curatedsyncexamples.fieldencryption.models.AndroidKeyStoreHelper
import io.realm.curatedsyncexamples.fieldencryption.ui.keystore.KeyStoreScreen
import io.realm.curatedsyncexamples.fieldencryption.ui.login.LoginScreen
import io.realm.curatedsyncexamples.fieldencryption.ui.records.SecretRecordScreen
import io.realm.kotlin.mongodb.App
import org.koin.compose.koinInject

object Screens {
    const val LOGIN_SCREEN = "LOGIN_SCREEN"
    const val KEYSTORE_PASSWORD_SCREEN = "KEYSTORE_PASSWORD"
    const val SECRET_RECORDS_SCREEN = "SECRET_RECORDS_SCREEN"
}

class NavGraphViewModel(
    private val app: App,
    private val keyAlias: String
) : ViewModel() {
    fun isUserLoggedIn(): Boolean = app.currentUser != null

    fun isFieldEncryptionKeyAvailable(): Boolean = AndroidKeyStoreHelper.containsKey(keyAlias)
}

@Composable
fun NavGraph(
    viewModel: NavGraphViewModel = koinInject(),
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String =
        when {
            !viewModel.isUserLoggedIn() -> Screens.LOGIN_SCREEN
            !viewModel.isFieldEncryptionKeyAvailable() -> Screens.KEYSTORE_PASSWORD_SCREEN
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
            LoginScreen {
                navActions.navigateToKeyStoreUnlockScreen()
            }
        }
        composable(
            Screens.KEYSTORE_PASSWORD_SCREEN,
        ) {
            KeyStoreScreen {
                navActions.navigateToSecretRecordsScreen()
            }
        }
        composable(
            Screens.SECRET_RECORDS_SCREEN,
        ) {
            SecretRecordScreen {
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
