package io.github.imnottimaq.anixartpc.Views

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.imnottimaq.anixartpc.Models
import io.github.imnottimaq.anixartpc.Net
import io.github.imnottimaq.anixartpc.client

@Composable
fun ReleasesScreen(releases: List<Models.Release>) {
    LazyColumn {
        items(releases) { release: Models.Release ->
            Text(release.title)
            Text(release.year ?: "")
        }
    }
}
