package io.github.imnottimaq.anixartpc.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import anixartpc.composeapp.generated.resources.Res
import anixartpc.composeapp.generated.resources.loading
import org.jetbrains.compose.resources.painterResource
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
    if (results.equals(listOf(Models.Release(id = 1, posterCacheName = "stub", title = "stub")))) {
        Image(
            painter = painterResource(Res.drawable.loading),
            contentDescription = "Loading",
            modifier = Modifier.fillMaxSize()
        )
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
