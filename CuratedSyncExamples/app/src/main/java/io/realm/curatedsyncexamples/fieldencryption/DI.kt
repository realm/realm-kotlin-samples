package io.realm.curatedsyncexamples.fieldencryption

import io.realm.curatedsyncexamples.fieldencryption.ui.NavGraphViewModel
import io.realm.curatedsyncexamples.fieldencryption.ui.keystore.KeyStoreViewModel
import io.realm.curatedsyncexamples.fieldencryption.ui.login.LoginViewModel
import io.realm.curatedsyncexamples.fieldencryption.ui.records.SecretRecordsViewModel
import io.realm.kotlin.mongodb.App
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val FIELD_LEVEL_ENCRYPTION_KEY_ALIAS = "fieldLevelEncryptionKey"

val fieldEncryptionModule = module {
    viewModel { KeyStoreViewModel(get(), FIELD_LEVEL_ENCRYPTION_KEY_ALIAS) }
    viewModel { LoginViewModel(get()) }
    viewModel { SecretRecordsViewModel(get(), FIELD_LEVEL_ENCRYPTION_KEY_ALIAS) }

    viewModel { NavGraphViewModel(get(), FIELD_LEVEL_ENCRYPTION_KEY_ALIAS) }

    single { App.create("cypher-scjvs") }
}