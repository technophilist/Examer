package com.example.examer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding

/**
 * A data class that models a destination in a navigation drawer.
 */
data class NavigationDrawerDestination(
    val icon: ImageVector,
    val name: String,
    val onClick: () -> Unit,
)

/**
 * A scaffold that manages the topAppbar and NavigationDrawer.
 *
 * @param modifier the Modifier to be applied to the composable.
 * @param scaffoldState  state of this scaffold widget. It contains
 * the state of the screen, e.g. variables to provide manual control
 * over the drawer behavior, sizes of components, etc.
 * @param onNavigationIconClick callback that will be executed when
 * the navigation icon is clicked.
 * @param onNavigationItemClick callback that will be executed when
 * a navigation item in the navigation drawer is clicked. The lambda
 * receives an integer representing the index of the selected item.
 * @param currentlySelectedNavigationDrawerItemIndex the index of the
 * currently selected navigation item in the drawer.
 * @param  navigationDrawerDestinations a list of
 * [NavigationDrawerDestination]s that are to be added to the
 * navigation drawer.
 * @param content content of the current screen. The lambda receives
 * an implementation of [PaddingValues]that should be applied to the
 * content root via Modifier.padding to properly offset top and bottom bars.
 * If you're using VerticalScroller,apply this modifier to the child of the
 * scroller, and not on the scroller itself.
 */
@Composable
fun ExamerNavigationScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onNavigationIconClick: (() -> Unit)? = null,
    onNavigationItemClick: ((index: Int) -> Unit)? = null,
    currentlySelectedNavigationDrawerItemIndex: Int = 0,
    navigationDrawerDestinations: List<NavigationDrawerDestination>,
    content: @Composable (PaddingValues) -> Unit,
) {
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
                NavigationDrawerItem(
                    icon = item.icon,
                    label = item.name,
                    isSelected = currentlySelectedNavigationDrawerItemIndex == index,
                    onClick = {
                        onNavigationItemClick?.invoke(index)
                    }
                )
            }
        },
        content = content
    )
}

/**
 * An appbar customized for Examer.
 * @param modifier the modifier to be applied to the composable.
 * @param onNavigationIconClick an optional callback that will be
 * executed when the navigation icon is clicked.
 */
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
