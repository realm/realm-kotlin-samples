package io.realm.curatedsyncexamples.fieldencryption

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import io.realm.curatedsyncexamples.fieldencryption.ui.NavGraph
import io.realm.curatedsyncexamples.ui.theme.CuratedSyncExamplesTheme
import io.realm.kotlin.mongodb.App

class FieldEncryptionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CuratedSyncExamplesTheme {
                NavGraph()
            }
        }
    }
}
