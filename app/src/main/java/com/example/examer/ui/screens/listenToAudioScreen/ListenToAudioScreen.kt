package com.example.examer.ui.screens.listenToAudioScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.example.examer.ui.components.NonClickableTestInfoChip
import com.example.examer.utils.conditional
import com.google.accompanist.insets.systemBarsPadding


@Composable
fun ListenToAudioScreen(
    audioPlayBackState: AudioPlaybackState,
    onNavigateToWorkBook: () -> Unit,
    onAudioIconClick: () -> Unit = {},
    isAudioIconClickEnabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.label_listen_and_answer),
                style = MaterialTheme.typography.h5
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.80f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .conditional(isAudioIconClickEnabled) { clickable(onClick = onAudioIconClick) }
                        .background(MaterialTheme.colors.primary.copy(alpha = if (!isAudioIconClickEnabled) ContentAlpha.disabled else DefaultAlpha))
                        .padding(16.dp),
                    imageVector = Icons.Filled.VolumeUp,
                    contentDescription = null,
                    tint = contentColorFor(backgroundColor = MaterialTheme.colors.primary)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(
                        R.string.label_no_of_repeats_left,
                        audioPlayBackState.numberOfRepeatsLeft.value
                    ),
                    style = MaterialTheme.typography.caption
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    modifier = Modifier.clip(RoundedCornerShape(50)),
                    progress = audioPlayBackState.progress.value
                )
            }
        }
        Button(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = onNavigateToWorkBook
        ) {
            Text(text = stringResource(R.string.button_label_go_to_workbook))
            Icon(imageVector = Icons.Filled.NavigateNext, contentDescription = null)
        }
    }
}