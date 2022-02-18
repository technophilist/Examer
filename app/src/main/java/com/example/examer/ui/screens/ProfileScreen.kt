package com.example.examer.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.examer.R
import com.example.examer.data.domain.ExamerUser
import com.example.examer.ui.navigation.ExamerDestinations
import com.example.examer.viewmodels.ProfileScreenViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import java.lang.IllegalArgumentException

data class UserAttribute(
    val label: String,
    val value: String,
    val onClick: () -> Unit
)

private sealed class DefaultExamerProfileScreenDestinations(val route: String) {
    object ProfileScreen : DefaultExamerProfileScreenDestinations(
        route = "examer.ui.screens.DefaultProfileScreenDestinations.PROFILE_SCREEN_ROUTE"
    )

    object EditScreen : DefaultExamerProfileScreenDestinations(
        route = "examer.ui.screens.DefaultProfileScreenDestinations.EDIT_SCREEN_ROUTE/{nameOfValueToEdit}"
    )
}

/**
 * A stateful implementation of [ProfileScreen].
 */
@ExperimentalCoilApi
@Composable
fun DefaultExamerProfileScreen(
    currentlyLoggedInUser: ExamerUser,
    onEditProfilePictureButtonClick: (image: ImageBitmap) -> Unit,
    updateName: (newName: String) -> Unit,
    updateEmail: (newEmail: String) -> Unit,
    updatePassword: (newPassword: String) -> Unit
) {
    val navController = rememberNavController()
    // need to pass an empty string if photoUrl is null
    // else the error drawable will not be visible
    val profileScreenImagePainter = rememberImagePainter(
        data = currentlyLoggedInUser.photoUrl ?: "",
        builder = {
            error(R.drawable.blank_profile_picture)
            crossfade(true)
        }
    )

    val resources = LocalContext.current.resources
    val profileScreenUserAttributes = listOf(
        UserAttribute(
            label = resources.getString(R.string.label_name),
            value = currentlyLoggedInUser.name,
            onClick = {
                navController.navigate("${DefaultExamerProfileScreenDestinations.EditScreen.route}/name")
            }
        ),
        UserAttribute(
            label = resources.getString(R.string.label_email_address),
            value = currentlyLoggedInUser.email,
            onClick = {
                navController.navigate("${DefaultExamerProfileScreenDestinations.EditScreen.route}/email")
            }
        ),
        UserAttribute(
            label = resources.getString(R.string.label_password),
            value = "**********",
            onClick = {
                navController.navigate("${DefaultExamerProfileScreenDestinations.EditScreen.route}/password")
            }
        )
    )
    NavHost(
        navController = navController,
        startDestination = DefaultExamerProfileScreenDestinations.ProfileScreen.route
    ) {
        composable(DefaultExamerProfileScreenDestinations.ProfileScreen.route) {
            ProfileScreen(
                imagePainter = profileScreenImagePainter,
                onEditProfilePictureButtonClick = {
                    navController.navigate(DefaultExamerProfileScreenDestinations.EditScreen.route)
                },
                userAttributes = profileScreenUserAttributes
            )
        }
        composable(
            route = "${DefaultExamerProfileScreenDestinations.EditScreen.route}/{nameOfValueToEdit}",
            arguments = listOf(
                navArgument(name = "nameOfValueToEdit") {
                    nullable = false
                    type = NavType.StringType
                }
            )
        ) { backstackEntry ->
            var textFieldValue by remember { mutableStateOf("") }
            backstackEntry.arguments?.let { arguments ->
                val nameOfValueToBeEdited = arguments["nameOfValueToEdit"].toString()
                EditScreen(
                    nameOfValueToBeEdited = nameOfValueToBeEdited,
                    textFieldValue = textFieldValue,
                    onTextFieldValueChange = { textFieldValue = it },
                    onSaveButtonClick = {
                        when (nameOfValueToBeEdited) {
                            "name" -> updateName(textFieldValue)
                            "email" -> updateEmail(textFieldValue)
                            "password" -> updatePassword(textFieldValue)
                            else -> throw IllegalArgumentException(nameOfValueToBeEdited)
                        }
                        navController.navigate(DefaultExamerProfileScreenDestinations.ProfileScreen.route)
                    }
                )
            }
        }
    }
}

/**
 * An edit screen for [DefaultExamerProfileScreen]
 */
@Composable
private fun EditScreen(
    nameOfValueToBeEdited: String,
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    onSaveButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Enter the new ${nameOfValueToBeEdited.lowercase()}") // TODO string res
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldValue,
            onValueChange = onTextFieldValueChange
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSaveButtonClick
        ) {
            Text(text = "Save") // TODO string res
        }
    }
}

/**
 * Stateless implementation of profile screen
 */
@ExperimentalCoilApi
@Composable
fun ProfileScreen(
    imagePainter: ImagePainter,
    onEditProfilePictureButtonClick: () -> Unit,
    userAttributes: List<UserAttribute>,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth()
                .background(Color.LightGray),
            onEditProfilePictureButtonClick = onEditProfilePictureButtonClick,
            painter = imagePainter
        )
        // TODO Make column scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            userAttributes.forEach {
                UserAttributeItem(
                    name = it.label,
                    value = it.value,
                    onClick = it.onClick
                )
                Divider()
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
private fun Header(
    painter: ImagePainter,
    modifier: Modifier = Modifier,
    onEditProfilePictureButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .placeholder(
                    visible = painter.state is ImagePainter.State.Loading,
                    highlight = PlaceholderHighlight.shimmer()
                ),
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onEditProfilePictureButtonClick) {
            Text(text = stringResource(R.string.button_label_edit_profile_picture))
        }
    }
}

@Composable
private fun UserAttributeItem(
    name: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h6
            )
            Text(
                modifier = Modifier.alpha(ContentAlpha.medium),
                text = value,
                style = MaterialTheme.typography.subtitle1,
            )
        }
        Icon(
            modifier = Modifier.align(Alignment.CenterVertically),
            imageVector = Icons.Filled.NavigateNext,
            contentDescription = null
        )
    }
}