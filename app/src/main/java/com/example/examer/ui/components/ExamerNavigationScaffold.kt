package com.example.examer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.example.examer.data.domain.ExamerUser
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding

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
 * @param currentlyLoggedInUser the currently authenticated user. The
 * name and email of the user will be displayed in the header of the
 * navigation drawer.
 * @param modifier the Modifier to be applied to the composable.
 * @param scaffoldState  state of this scaffold widget. It contains
 * the state of the screen, e.g. variables to provide manual control
 * over the drawer behavior, sizes of components, etc.
 * @param onNavigationIconClick callback that will be executed when
 * the navigation icon is clicked.
 * @param isNavigationDrawerDestinationSelected the lambda that will
 * be executed in-order to resolve whether an item in the navigation
 * drawer is selected.
 * @param  navigationDrawerDestinations a list of
 * [NavigationDrawerDestination]s that are to be added to the
 * navigation drawer.
 * @param onSignOutButtonClick the callback that will be executed when
 * the sign out button located at the bottom of the navigation drawer
 * is clicked.
 * @param content content of the current screen. The lambda receives
 * an implementation of [PaddingValues]that should be applied to the
 * content root via Modifier.padding to properly offset top and bottom bars.
 * If you're using VerticalScroller,apply this modifier to the child of the
 * scroller, and not on the scroller itself.
 */
@Composable
fun ExamerNavigationScaffold(
    currentlyLoggedInUser: ExamerUser,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onNavigationIconClick: (() -> Unit)? = null,
    isNavigationDrawerDestinationSelected: ((NavigationDrawerDestination) -> Boolean)? = null,
    navigationDrawerDestinations: List<NavigationDrawerDestination>,
    onSignOutButtonClick: (() -> Unit)? = null,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                NavigationDrawerHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .weight(0.13f),
                    currentlyLoggedInUser = currentlyLoggedInUser
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .weight(0.77f),
                ) {
                    navigationDrawerDestinations.forEach { item ->
                        NavigationDrawerItem(
                            icon = item.icon,
                            label = item.name,
                            isSelected = isNavigationDrawerDestinationSelected?.invoke(item)
                                ?: false,
                            onClick = item.onClick
                        )
                    }
                }
                // Footer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .weight(0.1f)
                ) {
                    Divider(modifier = Modifier.fillMaxWidth())
                    NavigationDrawerItem(
                        icon = Icons.Filled.Logout,
                        label = stringResource(R.string.button_sign_out),
                        isSelected = false,
                        onClick = onSignOutButtonClick
                    )
                }
            }
        },
        content = content
    )
}

@Composable
private fun NavigationDrawerHeader(
    currentlyLoggedInUser: ExamerUser,
    modifier: Modifier = Modifier,
) {
    // TODO add elipses
    Column(modifier = modifier) {
        val paddingStartModifier = Modifier.padding(start = 16.dp)
        Text(
            modifier = paddingStartModifier,
            text = currentlyLoggedInUser.name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            modifier = paddingStartModifier,
            text = currentlyLoggedInUser.email,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(modifier = Modifier.fillMaxWidth())
    }
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
