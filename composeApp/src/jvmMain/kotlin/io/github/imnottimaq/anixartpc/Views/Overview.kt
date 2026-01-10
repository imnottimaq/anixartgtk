package io.github.imnottimaq.anixartpc.Views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OverviewScreen(
    currentScreen: Int
) {
    Text("Current screen is: $currentScreen")
}