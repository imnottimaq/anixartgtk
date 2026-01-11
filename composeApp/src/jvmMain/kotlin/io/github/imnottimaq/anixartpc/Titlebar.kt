package io.github.imnottimaq.anixartpc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import androidx.compose.ui.window.WindowState

@Composable
fun rememberMaximizeController(windowState: WindowState): MaximizeController =
    remember(windowState) { MaximizeController(windowState) }

val buttons = listOf("Home", "Explore", "Bookmarks", "Account")
val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Explore, Icons.Filled.Bookmark, Icons.Filled.Person)
val unselectedIcons = listOf(Icons.Default.Home, Icons.Default.Explore, Icons.Default.Bookmark, Icons.Default.Person)

@Composable
fun WindowScope.TitleBar(
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchExpanded: Boolean,
    onSearchOpen: () -> Unit,
    onSearchBack: () -> Unit,
    onReleaseBack: () -> Unit,
    state: WindowState,
    currentScreen: Int,
    onCurrentScreenChange: (Int) -> Unit,
    isMac: Boolean
) {
    val max = rememberMaximizeController(state)

    WindowDraggableArea(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val showBack = searchExpanded || currentScreen == 5
            val leftIcon = if (showBack) Icons.Filled.ArrowBack else Icons.Filled.Search
            val leftDesc = if (showBack) "Back" else "Search"
            val leftAction = when {
                searchExpanded -> onSearchBack
                currentScreen == 5 -> onReleaseBack
                else -> onSearchOpen
            }

            SearchIconButton(
                onClick = leftAction,
                icon = leftIcon,
                contentDescription = leftDesc
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (searchExpanded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(searchExpanded){
                            val interactionSource = remember { MutableInteractionSource() }
                            val textFieldColors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.outline,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                            )
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .padding(horizontal = 6.dp)
                            ) { innerTextField ->
                                TextFieldDefaults.DecorationBox(
                                    value = searchQuery,
                                    innerTextField = innerTextField,
                                    enabled = true,
                                    singleLine = true,
                                    visualTransformation = VisualTransformation.None,
                                    interactionSource = interactionSource,
                                    placeholder = { Text("Search...") },
                                    colors = textFieldColors,
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ){
                buttons.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 6.dp)
                            .clickable { onCurrentScreenChange(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentScreen == index) selectedIcons[index] else unselectedIcons[index],
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(item)
                        }
                    }
                }
            }

            if (!isMac) {
                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WindowButton(onClick = onMinimize, icon = Icons.Filled.Remove, contentDescription = "Minimize")

                    WindowButton(
                        onClick = { max.toggle() },
                        icon = if (max.isMaximized) Icons.Filled.FilterNone else Icons.Filled.CropSquare,
                        contentDescription = if (max.isMaximized) "Restore" else "Maximize"
                    )

                    WindowButton(onClick = onClose, icon = Icons.Filled.Close, contentDescription = "Close")
                }
            }
        }
    }
}

@Composable
fun SearchIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .size(40.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(18.dp))
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
            .size(40.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(16.dp).rotate(rotation)
        )
    }
}

