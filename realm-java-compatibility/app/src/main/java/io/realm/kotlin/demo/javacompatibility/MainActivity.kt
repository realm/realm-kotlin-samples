package io.realm.kotlin.demo.javacompatibility

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.realm.kotlin.demo.javacompatibility.data.java.JavaRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
