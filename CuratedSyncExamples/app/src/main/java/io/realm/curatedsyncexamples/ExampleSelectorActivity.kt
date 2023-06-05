package io.realm.curatedsyncexamples

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.realm.curatedsyncexamples.fieldencryption.MainActivity
import io.realm.curatedsyncexamples.ui.theme.CuratedSyncExamplesTheme

class ExampleSelectorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CuratedSyncExamplesTheme {
                // A surface container using the 'background' color from the theme
                ExamplesScreen(entries)
            }

        }
    }
}

val entries = arrayOf(
    ExampleEntry(
        name = "Field level encryption",
        activity = MainActivity::class.java
    ),
)

data class ExampleEntry(
    val name: String,
    val activity: Class<*>
)

@Composable
fun ExamplesScreen(examplesList: Array<ExampleEntry>) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(48.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.realmio_logo_vector),
                contentDescription = "Realm logo",
            )
            Text(
                text = "Reference app that showcases different design patterns and examples of Realm Kotlin SDK with Atlas",
                modifier = Modifier.padding(vertical = 48.dp),
                textAlign = TextAlign.Center
            )
            examplesList.forEach { example ->
                ExampleEntry(example.name) {
                    context.startActivity(Intent(context, example.activity))
                }
            }
        }
    }
}

@Composable
fun ExampleEntry(name: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = name,
            modifier = modifier
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ExamplesScreenPreview() {
    CuratedSyncExamplesTheme {
        ExamplesScreen(entries)
    }
}