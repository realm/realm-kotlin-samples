package io.realm.curatedsyncexamples.fieldencryption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import io.realm.curatedsyncexamples.fieldencryption.ui.NavGraph
import io.realm.curatedsyncexamples.fieldencryption.ui.records.SecretRecordsViewModel
import io.realm.curatedsyncexamples.ui.theme.CuratedSyncExamplesTheme
import io.realm.kotlin.mongodb.App

const val FIELD_LEVEL_ENCRYPTION_KEY_ALIAS = "fieldLevelEncryptionKey"
const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = App.create("cypher-scjvs")

        setContent {
            CuratedSyncExamplesTheme {
                NavGraph(app)
            }
        }
    }
}
