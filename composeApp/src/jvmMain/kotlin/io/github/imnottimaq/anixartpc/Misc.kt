package io.github.imnottimaq.anixartpc

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import java.awt.Frame
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.awt.Toolkit
import java.awt.Window

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
    private var savedBounds: Rectangle? = null
    private var windowRef: Window? = null
    var isMaximized by mutableStateOf(false)
        private set

    fun attachWindow(window: Window) {
        windowRef = window
    }

    fun toggle() {
        val currentlyMaximized = isMaximized

        if (!currentlyMaximized) {
            val frame = windowRef as? Frame
            if (frame != null) {
                savedBounds = frame.bounds
                val gc = frame.graphicsConfiguration
                val bounds = gc.bounds
                val insets = Toolkit.getDefaultToolkit().getScreenInsets(gc)
                val x = bounds.x + insets.left
                val y = bounds.y + insets.top
                val w = bounds.width - insets.left - insets.right
                val h = bounds.height - insets.top - insets.bottom
                frame.setBounds(x, y, w, h)
                isMaximized = true
            } else {
                savedSize = state.size
                savedPos = state.position
                val bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
                state.size = DpSize(bounds.width.dp, bounds.height.dp)
                state.position = androidx.compose.ui.window.WindowPosition.Absolute(
                    x = bounds.x.dp,
                    y = bounds.y.dp
                )
                isMaximized = true
            }
        } else {
            val frame = windowRef as? Frame
            if (frame != null) {
                frame.extendedState = Frame.NORMAL
                savedBounds?.let { frame.bounds = it }
                savedBounds = null
                isMaximized = false
            } else {
                state.size = savedSize ?: state.size
                state.position = savedPos ?: androidx.compose.ui.window.WindowPosition.PlatformDefault
                savedSize = null
                savedPos = null
                isMaximized = false
            }
        }
    }
}
