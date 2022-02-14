package com.example.examer.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.examer.R
import com.example.examer.data.domain.ExamerUser
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

data class UserAttribute(
    val label: String,
    val value: String,
    val onClick: () -> Unit
)

@ExperimentalCoilApi
@Composable
fun ProfileScreen(
    examerUser: ExamerUser,
    onEditProfilePictureButtonClick: () -> Unit,
    userAttributes: List<UserAttribute>,
) {
    // need to pass an empty string if photoUrl is null
    // else the error drawable will not be visible
    val imagePainter = rememberImagePainter(
        data = examerUser.photoUrl ?: "",
        builder = {
            error(R.drawable.blank_profile_picture)
            crossfade(true)
        }
    )
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