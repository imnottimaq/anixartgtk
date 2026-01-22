package io.github.imnottimaq.anixartpc

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState

@Composable
fun rememberMaximizeController(windowState: WindowState): MaximizeController =
    remember(windowState) { MaximizeController(windowState) }
private val buttons = listOf("","","","","","","")
private val selectedIcons = listOf(
    Icons.Filled.Home,
    Icons.Filled.Explore,
    Icons.Filled.Bookmark,
    Icons.Filled.Person,
    Icons.Filled.Search,
    Icons.Filled.Notifications,
    Icons.Filled.Settings
)
private val unselectedIcons = listOf(
    Icons.Outlined.Home,
    Icons.Outlined.Explore,
    Icons.Outlined.Bookmark,
    Icons.Outlined.Person,
    Icons.Outlined.Search,
    Icons.Outlined.Notifications,
    Icons.Outlined.Settings
)

@Composable
fun WindowScope.Sidebar(
    currentScreen: Int,
    onCurrentScreenChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(40.dp)
    ){
        buttons.forEachIndexed { index, button ->
            IconButton(
                onClick = {
                    if (index <= 4) {
                        onCurrentScreenChange(index)
                    }
                },
                shape = RectangleShape,
            ){
                Icon(imageVector = if (currentScreen == index) selectedIcons[index] else unselectedIcons[index],contentDescription = null)
            }
        }
    }
}

@Composable
fun WindowScope.Titlebar(
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    maximizeController: MaximizeController,
    searchExpanded: Boolean,
    onSearchOpen: () -> Unit,
    onSearchBack: () -> Unit,
    onReleaseBack: () -> Unit,
    currentScreen: Int,
    isMac: Boolean
) {
    val titlebarModifier = Modifier
        .fillMaxWidth()
        .height(20.dp)
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = { maximizeController.toggle() }
            )
        }

    val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentScreen == 5) {
                WindowButton(
                    onClick = { onReleaseBack() },
                    icon = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            } else {
                Box(modifier = Modifier.size(20.dp))
            }

            Box(modifier = Modifier.weight(1f).fillMaxHeight())

            if (!isMac) {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WindowButton(onClick = onMinimize, icon = Icons.Filled.Remove, contentDescription = "Minimize")

                    WindowButton(
                        onClick = { maximizeController.toggle() },
                        icon = if (maximizeController.isMaximized) Icons.Filled.FilterNone else Icons.Filled.CropSquare,
                        contentDescription = if (maximizeController.isMaximized) "Restore" else "Maximize"
                    )

                    WindowButton(onClick = onClose, icon = Icons.Filled.Close, contentDescription = "Close")
                }
            }
        }
    }

    if (maximizeController.isMaximized) {
        Box(modifier = titlebarModifier) { content() }
    } else {
        WindowDraggableArea(modifier = titlebarModifier) { content() }
    }
}

@Composable
fun WindowButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    rotation: Float = 0f,
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(12.dp).rotate(rotation)
        )
    }
}
