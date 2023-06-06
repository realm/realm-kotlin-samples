package io.realm.curatedsyncexamples

import android.app.Application
import io.realm.curatedsyncexamples.fieldencryption.fieldEncryptionModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class CuratedSyncExamplesApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@CuratedSyncExamplesApp)
            // Load modules
            modules(fieldEncryptionModule)
        }
    }
}
