package com.example.examer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Data class that holds the colors that are related to
 * [NavigationDrawerItem].
 * @param backgroundColor the background color of the item.
 * @param contentColor the color of the text and icon inside
 * the item.
 */
data class NavigationDrawerItemColors(
    val backgroundColor: Color,
    val contentColor: Color,
)

/**
 * Contains the default values used by [NavigationDrawerItem].
 */
object NavigationDrawerItemDefaults {

    /**
     * The default [NavigationDrawerItemColors] to use when the item
     * is selected.
     */
    val activeColors: NavigationDrawerItemColors
        @Composable get() = NavigationDrawerItemColors(
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.12f),
            contentColor = MaterialTheme.colors.primary
        )

    /**
     * The default [NavigationDrawerItemColors] to use when the item
     * is not selected.
     */
    val inactiveColors: NavigationDrawerItemColors
        @Composable get() = NavigationDrawerItemColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
        )

    /**
     * The default shape to use for the navigation item.
     */
    val shape = RoundedCornerShape(12)
}

/**
 * A navigation item that is meant to be used within a Navigation Drawer.
 *
 * @param icon the icon to be used for the item.
 * @param label the label the corresponds to the item.
 * @param isSelected determines whether the item is selected.
 * @param onClick the callback to run when the item is clicked.
 * @param activeColors the [NavigationDrawerItemColors] to use when the
 * button is active.
 * @param inactiveColors the [NavigationDrawerItemColors] to use when the
 * button is inactive.
 * @param shape the shape of the navigation item.
 */
@Composable
fun NavigationDrawerItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    activeColors: NavigationDrawerItemColors = NavigationDrawerItemDefaults.activeColors,
    inactiveColors: NavigationDrawerItemColors = NavigationDrawerItemDefaults.inactiveColors,
    shape: RoundedCornerShape = NavigationDrawerItemDefaults.shape,
) {
    val rowBackgroundColor = if (isSelected) activeColors.backgroundColor
    else inactiveColors.backgroundColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .clip(shape)
            .background(rowBackgroundColor)
            .clickable { onClick?.invoke() }
            .padding(8.dp)
    ) {
        val iconAndTextColor = if (isSelected) activeColors.contentColor
        else inactiveColors.contentColor

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconAndTextColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, color = iconAndTextColor)
    }
}