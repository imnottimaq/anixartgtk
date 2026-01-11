package io.github.imnottimaq.anixartpc.Views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.imnottimaq.anixartpc.Models

@Composable
fun SearchScreen(
    query: String,
    results: List<Models.Release>,
    onReleaseClick: (Int) -> Unit,
) {
    println()
    if (query.isBlank()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Start typing to search")
        }
        return
    }

    if (results.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No results")
        }
        return
    }

    ReleasesScreen(results, onReleaseClick = onReleaseClick)
}
