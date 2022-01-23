package com.example.examer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding


data class NavigationDrawerDestination(
    val icon: ImageVector,
    val name: String,
    val onClick: () -> Unit,
)

@Composable
fun ExamerNavigation(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onNavigationIconClick: (() -> Unit)? = null,
    onNavigationItemClick: ((index: Int) -> Unit)? = null,
    currentlySelectedNavigationDrawerItemIndex: Int = 0,
    navigationDrawerDestinations: List<NavigationDrawerDestination>,
    content: @Composable (PaddingValues) -> Unit,
) {
    // TODO State hoisting
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = {
            ExamerAppbar(
                modifier = Modifier
                    .background(MaterialTheme.colors.primarySurface)
                    .statusBarsPadding(),
                onNavigationIconClick = onNavigationIconClick
            )
        },
        drawerContent = {
            Spacer(modifier = Modifier.statusBarsHeight(16.dp))
            navigationDrawerDestinations.forEachIndexed { index, item ->
                DrawerItem(
                    navigationDrawerItem = item,
                    isSelected = currentlySelectedNavigationDrawerItemIndex == index,
                    onItemSelected = { onNavigationItemClick?.invoke(index) }
                )
            }
        },
        content = content
    )
}

@Composable
private fun ExamerAppbar(
    modifier: Modifier = Modifier,
    onNavigationIconClick: (() -> Unit)? = null
) {
    val navigationIcon = @Composable {
        IconButton(
            onClick = { onNavigationIconClick?.invoke() },
            content = {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }
        )
    }
    TopAppBar(
        modifier = modifier,
        title = { Text(stringResource(id = R.string.app_name)) },
        navigationIcon = navigationIcon,
    )
}

@Composable
private fun DrawerItem(
    isSelected: Boolean = false,
    onItemSelected: (() -> Unit)? = null,
    navigationDrawerItem: NavigationDrawerDestination,
) {
    //TODO Hoist color state
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .clip(RoundedCornerShape(12))
            .background(
                if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.12f)
                else MaterialTheme.colors.surface
            )
            .clickable {
                navigationDrawerItem.onClick()
                onItemSelected?.invoke()
            }
            .padding(8.dp)
    ) {
        val color = if (isSelected) MaterialTheme.colors.primary
        else MaterialTheme.colors.onSurface
        Icon(
            imageVector = navigationDrawerItem.icon,
            contentDescription = null,
            tint = color
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = navigationDrawerItem.name, color = color)
    }
}

