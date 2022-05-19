package com.example.examer.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.examer.R
import com.example.examer.data.domain.ExamerUser
import com.example.examer.ui.components.CircularLoadingProgressOverlay
import com.example.examer.ui.components.ExamerSingleLineTextField
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import timber.log.Timber
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
        route = "examer.ui.screens.DefaultProfileScreenDestinations.EDIT_SCREEN_ROUTE/{nameOfValueToEdit}/{previousValue}"
    ) {
        fun buildRoute(nameOfValueToBeEdited: String, previousValue: String) =
            "examer.ui.screens.DefaultProfileScreenDestinations.EDIT_SCREEN_ROUTE/$nameOfValueToBeEdited/$previousValue"
    }
}

/**
 * A stateful implementation of [ProfileScreen].
 */
@ExperimentalCoilApi
@Composable
fun DefaultExamerProfileScreen(
    currentlyLoggedInUser: ExamerUser,
    isLoadingOverlayVisible: Boolean,
    onNavigateToEditScreen: (() -> Unit)? = null,
    onNavigateFromEditScreen: (() -> Unit)? = null,
    updateProfilePicture: (image: ImageBitmap) -> Unit,
    updateName: (newName: String) -> Unit,
    updateEmail: (newEmail: String) -> Unit,
    updatePassword: (newPassword: String) -> Unit,
    isValidEmail: ((String) -> Boolean),
    isValidPassword: ((String) -> Boolean)
) {
    val navController = rememberNavController()
    val onDestinationChangedListener = remember {
        NavController.OnDestinationChangedListener { controller, _, _ ->
            val currentBackStackEntry = controller.currentBackStackEntry
            val previousBackStackEntry = controller.previousBackStackEntry
            // if the current user is navigating from the edit screen to
            // the profile screen (pop exit) then run the callback if
            // it is not null
            if (previousBackStackEntry?.destination?.route == null &&
                currentBackStackEntry?.destination?.route == DefaultExamerProfileScreenDestinations.ProfileScreen.route
            ) onNavigateFromEditScreen?.invoke()
        }
    }
    // need to pass an empty string if photoUrl is null
    // else the error drawable will not be visible
    val profileScreenImagePainter = rememberImagePainter(
        data = currentlyLoggedInUser.photoUrl ?: "",
        builder = {
            error(R.drawable.blank_profile_picture)
            crossfade(true)
        }
    )
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap -> bitmap?.let { updateProfilePicture(it.asImageBitmap()) } }
    )
    val resources = LocalContext.current.resources
    val onProfileScreeUserAttributeClick = { nameOfValueToBeEdited: String, previousValue: String ->
        onNavigateToEditScreen?.invoke()
        navController.navigate(
            DefaultExamerProfileScreenDestinations.EditScreen.buildRoute(
                nameOfValueToBeEdited,
                previousValue
            )
        )
    }
    val profileScreenUserAttributes = listOf(
        UserAttribute(
            label = resources.getString(R.string.label_name),
            value = currentlyLoggedInUser.name,
            onClick = { onProfileScreeUserAttributeClick("name", currentlyLoggedInUser.name) }
        ),
        UserAttribute(
            label = resources.getString(R.string.label_email_address),
            value = currentlyLoggedInUser.email,
            onClick = { onProfileScreeUserAttributeClick("email", currentlyLoggedInUser.email) }
        ),
        UserAttribute(
            label = resources.getString(R.string.label_password),
            value = "**********",
            onClick = { onProfileScreeUserAttributeClick("password", "********") }
        )
    )
    DisposableEffect(key1 = Unit) {
        navController.addOnDestinationChangedListener(onDestinationChangedListener)
        onDispose { navController.removeOnDestinationChangedListener(onDestinationChangedListener) }
    }
    CircularLoadingProgressOverlay(isOverlayVisible = isLoadingOverlayVisible) {
        NavHost(
            navController = navController,
            startDestination = DefaultExamerProfileScreenDestinations.ProfileScreen.route
        ) {
            composable(DefaultExamerProfileScreenDestinations.ProfileScreen.route) {
                val scrollState = rememberScrollState()
                ProfileScreen(
                    imagePainter = profileScreenImagePainter,
                    onEditProfilePictureButtonClick = { launcher.launch() },
                    userAttributes = profileScreenUserAttributes,
                    scrollState = scrollState
                )
            }
            composable(
                route = DefaultExamerProfileScreenDestinations.EditScreen.route,
                arguments = listOf(
                    navArgument(name = "nameOfValueToEdit") {
                        nullable = false
                        type = NavType.StringType
                    },
                    navArgument(name = "previousValue") {
                        nullable = false
                        type = NavType.StringType
                    }
                )
            ) { backstackEntry ->
                backstackEntry.arguments?.let { arguments ->
                    val nameOfValueToBeEdited = arguments["nameOfValueToEdit"].toString()
                    val previousValue by remember { mutableStateOf(arguments["previousValue"].toString()) }
                    val isTextFieldPlaceHolderVisible by remember(nameOfValueToBeEdited) {
                        mutableStateOf(nameOfValueToBeEdited == "password")
                    }
                    var textFieldValue by remember {
                        // initialise the value of the text field with the previous value
                        // if the isTextFieldPlaceHolderVisible is not set to true
                        mutableStateOf(
                            if (!isTextFieldPlaceHolderVisible) previousValue
                            else ""
                        )
                    }
                    val isSaveButtonEnabled by remember(textFieldValue) {
                        mutableStateOf(textFieldValue.isNotBlank() && textFieldValue != previousValue)
                    }
                    var isErrorMessageVisible by remember { mutableStateOf(false) }
                    val currentErrorMessage by remember(isErrorMessageVisible) {
                        mutableStateOf(
                            when (nameOfValueToBeEdited) {
                                "email" -> resources.getString(R.string.label_invalid_email)
                                "password" -> resources.getString(R.string.label_invalid_password)
                                else -> ""
                            }
                        )
                    }
                    val onSaveButtonClick = {
                        isErrorMessageVisible = when (nameOfValueToBeEdited) {
                            "email" -> !isValidEmail(textFieldValue)
                            "password" -> !isValidPassword(textFieldValue)
                            else -> false
                        }
                        if (!isErrorMessageVisible) {
                            // if the error message is not visible, then update the values
                            // and navigate to the profile screen.
                            when (nameOfValueToBeEdited) {
                                "name" -> updateName(textFieldValue)
                                "email" -> updateEmail(textFieldValue)
                                "password" -> updatePassword(textFieldValue)
                                else -> throw IllegalArgumentException(nameOfValueToBeEdited)
                            }
                            navController.popBackStack()
                            onNavigateFromEditScreen?.invoke()
                        }
                    }
                    val context = LocalContext.current
                    val onDoneKeyboardAction by rememberUpdatedState(newValue = {
                        if (isSaveButtonEnabled) onSaveButtonClick()
                        else Toast.makeText(
                            context,
                            resources.getString(
                                R.string.toast_same_new_value_error,
                                nameOfValueToBeEdited
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                    val keyboardActions = remember {
                        KeyboardActions(onDone = { onDoneKeyboardAction() })
                    }
                    EditScreen(
                        nameOfValueToBeEdited = nameOfValueToBeEdited,
                        textFieldPlaceHolder = if (isTextFieldPlaceHolderVisible) previousValue
                        else null,
                        textFieldValue = textFieldValue,
                        onTextFieldValueChange = { textFieldValue = it },
                        isSaveButtonEnabled = isSaveButtonEnabled,
                        onSaveButtonClick = onSaveButtonClick,
                        isErrorMessageVisible = isErrorMessageVisible,
                        errorMessage = currentErrorMessage,
                        keyboardActions = keyboardActions
                    )
                }
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
    isSaveButtonEnabled: Boolean,
    onSaveButtonClick: () -> Unit,
    textFieldPlaceHolder: String?,
    keyboardActions: KeyboardActions? = null,
    isErrorMessageVisible: Boolean = false,
    errorMessage: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(
                id = R.string.label_enter_value_to_be_edited,
                nameOfValueToBeEdited.lowercase()
            )
        )
        if (nameOfValueToBeEdited == "password") {
            var isPasswordVisible by remember { mutableStateOf(false) }
            val trailingIcon = @Composable {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
            ExamerSingleLineTextField(
                modifier = Modifier.fillMaxWidth(),
                value = textFieldValue,
                onValueChange = onTextFieldValueChange,
                placeholder = { textFieldPlaceHolder?.let { Text(text = it) } },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = trailingIcon,
                keyboardActions = keyboardActions ?: KeyboardActions.Default,
            )
        } else {
            ExamerSingleLineTextField(
                modifier = Modifier.fillMaxWidth(),
                value = textFieldValue,
                onValueChange = onTextFieldValueChange,
                placeholder = { textFieldPlaceHolder?.let { Text(text = it) } },
                keyboardActions = keyboardActions ?: KeyboardActions.Default,
            )
        }
        if (isErrorMessageVisible) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colors.error
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSaveButtonClick,
            enabled = isSaveButtonEnabled
        ) {
            Text(text = stringResource(id = R.string.button_label_save))
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
    scrollState: ScrollState
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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