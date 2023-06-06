package io.realm.curatedsyncexamples.fieldencryption

import io.realm.curatedsyncexamples.fieldencryption.ui.NavGraphViewModel
import io.realm.curatedsyncexamples.fieldencryption.ui.keystore.KeyStoreViewModel
import io.realm.curatedsyncexamples.fieldencryption.ui.login.LoginViewModel
import io.realm.curatedsyncexamples.fieldencryption.ui.records.SecretRecordsViewModel
import io.realm.kotlin.mongodb.App
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val fieldEncryptionModule = module {
    val keyAlias = "fieldLevelEncryptionKey"

    viewModel { KeyStoreViewModel(get(), keyAlias) }
    viewModel { LoginViewModel(get()) }
    viewModel { SecretRecordsViewModel(get(), keyAlias) }

    viewModel { NavGraphViewModel(get(), keyAlias) }

    single { App.create("cypher-scjvs") }
}