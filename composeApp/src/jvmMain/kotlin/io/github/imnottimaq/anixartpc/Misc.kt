package io.github.imnottimaq.anixartpc

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import java.awt.GraphicsEnvironment
import java.awt.Toolkit

@Composable
fun DisplayImageFromInternet(imageUrl: String) {
    val context = LocalPlatformContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(client)) }
            .build()
    }

    AsyncImage(
        model = ImageRequest.Builder(context).data(imageUrl).build(),
        imageLoader = imageLoader,
        contentDescription = null,
        modifier = Modifier.size(200.dp),
    )
}

@Stable
class MaximizeController(private val state: WindowState) {
    private var savedSize: DpSize? = null
    private var savedPos: androidx.compose.ui.window.WindowPosition? = null

    fun toggle() {
        val currentlyMaximized = savedSize != null

        if (!currentlyMaximized) {
            savedSize = state.size
            savedPos = state.position

            val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
            val gd = ge.defaultScreenDevice
            val gc = gd.defaultConfiguration
            val bounds = gc.bounds
            val insets = Toolkit.getDefaultToolkit().getScreenInsets(gc)

            val w = (bounds.width - insets.left - insets.right).dp
            val h = (bounds.height - insets.top - insets.bottom).dp

            state.size = DpSize(w, h)
            state.position = androidx.compose.ui.window.WindowPosition.Absolute(
                x = insets.left.dp,
                y = insets.top.dp
            )
        } else {
            state.size = savedSize!!
            state.position = savedPos ?: androidx.compose.ui.window.WindowPosition.PlatformDefault
            savedSize = null
            savedPos = null
        }
    }

    val isMaximized: Boolean get() = savedSize != null
}
