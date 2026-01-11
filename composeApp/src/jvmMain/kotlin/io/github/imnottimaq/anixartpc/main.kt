package io.github.imnottimaq.anixartpc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.imnottimaq.anixartpc.Views.AccountScreen
import io.github.imnottimaq.anixartpc.Views.BookmarksScreen
import io.github.imnottimaq.anixartpc.Views.OverviewScreen
import io.github.imnottimaq.anixartpc.Views.ReleasePage
import io.github.imnottimaq.anixartpc.Views.ReleasesScreen
import io.github.imnottimaq.anixartpc.Views.SearchScreen
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay

val client = HttpClient(CIO) { install(ContentNegotiation) {json()} }

fun main() = application {
    val state = rememberWindowState(width = 1024.dp, height = 720.dp)
    var query by remember { mutableStateOf("") }
    var currentScreen by remember { mutableStateOf(0) }
    var searchExpanded by remember { mutableStateOf(false) }
    var previousScreen by remember { mutableStateOf(0) }
    var releases by remember { mutableStateOf<List<Models.Release>>(emptyList()) }
    var searchResults by remember { mutableStateOf<List<Models.Release>>(emptyList()) }
    var selectedReleaseId by remember { mutableStateOf<Int?>(null) }
    var returnToSearchFromRelease by remember { mutableStateOf(false) }
    val isMac = System.getProperty("os.name").contains("Mac", ignoreCase = true)
    if (isMac) {
        System.setProperty("apple.awt.fullWindowContent", "true")
        System.setProperty("apple.awt.transparentTitleBar", "true")
        System.setProperty("apple.awt.windowTitleVisible", "false")
    }
    LaunchedEffect(Unit) {
        Net().getLatestReleases(client)
            .onSuccess { list -> releases = list }
            .onFailure { it.printStackTrace() }
    }
    LaunchedEffect(query, searchExpanded, currentScreen) {
        val isSearchActive = searchExpanded || currentScreen == 4
        if (!isSearchActive) return@LaunchedEffect
        if (query.isBlank()) {
            searchResults = emptyList()
            return@LaunchedEffect
        }
        delay(200)
        Net().getSearchResults(client, query)
            .onSuccess { list -> searchResults = list }
            .onFailure { it.printStackTrace() }
    }
    Window(
        onCloseRequest = ::exitApplication,
        undecorated = true,
        resizable = true,
        title = "AnixartPC",
        state = state,
    ) {
        Column(Modifier.fillMaxSize()) {
            TitleBar(
                state = state,
                onMinimize = { state.isMinimized = true },
                onClose = ::exitApplication,
                searchQuery = query,
                onSearchQueryChange = { query = it },
                searchExpanded = searchExpanded,
                onSearchOpen = {
                    previousScreen = currentScreen
                    currentScreen = 4
                    searchExpanded = true
                },
                onSearchBack = {
                    searchExpanded = false
                    if (currentScreen == 4) {
                        currentScreen = previousScreen
                    }
                },
                onReleaseBack = {
                    selectedReleaseId = null
                    if (returnToSearchFromRelease) {
                        currentScreen = 4
                        searchExpanded = true
                    } else {
                        currentScreen = 0
                    }
                    returnToSearchFromRelease = false
                },
                currentScreen = currentScreen,
                onCurrentScreenChange = { index ->
                    currentScreen = index
                    searchExpanded = index == 4
                },
                isMac = isMac
            )
            val onReleaseClick: (Int) -> Unit = { id ->
                selectedReleaseId = id
                returnToSearchFromRelease = searchExpanded || currentScreen == 4
                if (returnToSearchFromRelease) {
                    searchExpanded = false
                }
                currentScreen = 5
            }

            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (currentScreen) {
                    0 -> ReleasesScreen(releases, onReleaseClick = onReleaseClick)
                    1 -> OverviewScreen(currentScreen)
                    2 -> BookmarksScreen(currentScreen)
                    3 -> AccountScreen(currentScreen)
                    4 -> SearchScreen(query, searchResults, onReleaseClick)
                    5 -> {
                        val id = selectedReleaseId
                        if (id != null) {
                            ReleasePage(id)
                        } else {
                            ReleasesScreen(releases, onReleaseClick = onReleaseClick)
                        }
                    }
                }
            }

        }

    }
}
