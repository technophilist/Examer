package com.example.examer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.examer.R
import com.example.examer.data.domain.ExamerUser
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

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
 * @param imagePainter the painter to use for drawing the profile
 * picture in the header. A shimmer animation will be automatically
 * added when the value of the 'state' member variable of the
 * image painter is [ImagePainter.State.Loading].
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
@ExperimentalCoilApi
@Composable
fun ExamerNavigationScaffold(
    currentlyLoggedInUser: ExamerUser,
    imagePainter: ImagePainter,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navigationIconImageVector: ImageVector = Icons.Filled.Menu,
    onNavigationIconClick: (() -> Unit)? = null,
    isNavigationDrawerDestinationSelected: ((NavigationDrawerDestination) -> Boolean)? = null,
    navigationDrawerDestinations: List<NavigationDrawerDestination>,
    onSignOutButtonClick: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val navigationIcon = @Composable {
        IconButton(
            onClick = { onNavigationIconClick?.invoke() },
            content = {
                Icon(
                    imageVector = navigationIconImageVector,
                    contentDescription = null
                )
            }
        )
    }
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        snackbarHost = {
            // Snackbar host with navigation bars padding
            SnackbarHost(
                modifier = Modifier.navigationBarsPadding(),
                hostState = scaffoldState.snackbarHostState
            )
        },
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .background(MaterialTheme.colors.primarySurface)
                    .statusBarsPadding(),
                title = { Text(stringResource(id = R.string.app_name)) },
                navigationIcon = navigationIcon,
            )
        },
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
            ) {
                // Header
                NavigationDrawerHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(top = 16.dp),
                    currentlyLoggedInUser = currentlyLoggedInUser,
                    imagePainter = imagePainter,
                )
                Column(
                    modifier = Modifier
                        .weight(9f)
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Divider(modifier = Modifier.fillMaxWidth())
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
                        .weight(1f)
                        .fillMaxWidth()
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

@ExperimentalCoilApi
@Composable
private fun NavigationDrawerHeader(
    currentlyLoggedInUser: ExamerUser,
    modifier: Modifier = Modifier,
    imagePainter: ImagePainter
) {
    val paddingStartModifier = Modifier.padding(start = 16.dp)
    Row(
        modifier = modifier.then(paddingStartModifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(CircleShape)
                .placeholder(
                    visible = imagePainter.state is ImagePainter.State.Loading,
                    highlight = PlaceholderHighlight.shimmer()
                ),
            painter = imagePainter,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Column(modifier = paddingStartModifier) {
            Text(
                text = currentlyLoggedInUser.name,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = currentlyLoggedInUser.email,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                maxLines = 1,
            )
        }
    }
}

