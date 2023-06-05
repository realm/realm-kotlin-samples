package io.realm.curatedsyncexamples

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.realm.curatedsyncexamples.fieldencryption.MainActivity
import io.realm.curatedsyncexamples.ui.theme.CuratedSyncExamplesTheme

class ExampleSelectorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CuratedSyncExamplesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MessageList(entries)
                }
            }
        }
    }
}

val entries = arrayOf(
    ExampleEntry(
        name = "Field level encryption",
        activity = MainActivity::class.java
    ),
    ExampleEntry(
        name = "Field level encryption",
        activity = MainActivity::class.java
    )
)

data class ExampleEntry(
    val name: String,
    val activity: Class<*>
)

@Composable
fun MessageList(examplesList: Array<ExampleEntry>) {
    val context = LocalContext.current

    Column {
        examplesList.forEach { example ->
            Greeting(example.name) {
                context.startActivity(Intent(context, example.activity))
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(
            text = name,
            modifier = modifier
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CuratedSyncExamplesTheme {
        MessageList(entries)
    }
}