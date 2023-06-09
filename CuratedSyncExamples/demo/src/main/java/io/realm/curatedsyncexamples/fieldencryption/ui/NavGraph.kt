/*
 * Copyright 2023 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.realm.curatedsyncexamples.fieldencryption.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.realm.curatedsyncexamples.fieldencryption.models.SystemKeyStore
import io.realm.curatedsyncexamples.fieldencryption.ui.keystore.UnlockUserKeyStoreScreen
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

    fun isFieldEncryptionKeyAvailable(): Boolean = SystemKeyStore.containsKey(keyAlias)
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
            LoginScreen(
                modifier = modifier.verticalScroll(rememberScrollState())
            ) {
                navActions.navigateToKeyStoreUnlockScreen()
            }
        }
        composable(
            Screens.KEYSTORE_PASSWORD_SCREEN,
        ) {
            UnlockUserKeyStoreScreen(
                modifier = modifier.verticalScroll(rememberScrollState())
            ) {
                navActions.navigateToSecretRecordsScreen()
            }
        }
        composable(
            Screens.SECRET_RECORDS_SCREEN,
        ) {
            SecretRecordScreen(
                modifier = modifier.padding(horizontal = 16.dp)
            ) {
                navActions.navigateToLogin()
            }
        }
    }
}

class NavigationActions(private val navController: NavHostController) {
    fun navigateToKeyStoreUnlockScreen() {
        navController.navigate(Screens.KEYSTORE_PASSWORD_SCREEN) {
            navController.popBackStack()
            launchSingleTop = true
        }
    }

    fun navigateToSecretRecordsScreen() {
        navController.navigate(Screens.SECRET_RECORDS_SCREEN) {
            navController.popBackStack()
            launchSingleTop = true
        }
    }

    fun navigateToLogin() {
        navController.navigate(Screens.LOGIN_SCREEN) {
            navController.popBackStack()
            launchSingleTop = true
        }
    }
}
