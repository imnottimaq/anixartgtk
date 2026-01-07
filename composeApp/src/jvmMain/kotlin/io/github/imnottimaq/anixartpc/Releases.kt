package io.github.imnottimaq.anixartpc

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.ktor.client.HttpClient
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ReleasesScreen(client: HttpClient){
    var releases by remember { mutableStateOf<List<Models.Release>>(emptyList()) }
    LaunchedEffect(Unit) {
        Net().getLatestReleases(client)
            .onSuccess { list ->
                releases = list
            }
            .onFailure { it.printStackTrace() }
    }
    LazyColumn {
        items(releases) { release: Models.Release ->
            Text(release.title)
            Text(release.year ?: "")
        }
    }
}