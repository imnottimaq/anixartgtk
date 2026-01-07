package io.github.imnottimaq.anixartpc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import anixartpc.composeapp.generated.resources.Res
import anixartpc.composeapp.generated.resources.compose_multiplatform
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

val client = HttpClient { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true })}}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AnixartPC",
    ) {
        App()
    }
}

@Composable
actual fun App() {
    var showReleases by remember { mutableStateOf(false) }

    MaterialTheme {
        if (!showReleases) {
            Button(
                onClick = { showReleases = true }
            ) {
                Text("Load releases")
            }
        } else {
            ReleasesScreen(client = client)
        }
    }
}