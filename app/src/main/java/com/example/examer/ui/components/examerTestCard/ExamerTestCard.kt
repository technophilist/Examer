package com.example.examer.ui.components.examerTestCard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.example.examer.data.domain.Status
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.getDateStringAndTimeString

@ExperimentalMaterialApi
@Composable
fun ExamerExpandableTestCard(
    test: TestDetails,
    isExpanded: Boolean,
    onExpandButtonClick: (() -> Unit),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onTakeTestButtonClick: (() -> Unit)? = null,
    is24HourTimeFormat: Boolean = false
) {
    val (dateString, timeString) = test.getDateStringAndTimeString(is24hourFormat = is24HourTimeFormat)
    Card(
        modifier = modifier.animateContentSize(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            ExamerCardHeader(
                title = test.title,
                isExpanded = isExpanded,
                onExpandButtonClick = onExpandButtonClick
            )
            Text(text = test.language, style = MaterialTheme.typography.subtitle1)
            Spacer(modifier = Modifier.height(8.dp))
            StatusRow(modifier = Modifier.fillMaxWidth(), testStatus = test.testStatus)
            Spacer(modifier = Modifier.height(8.dp))
            ExamerCardMetadataRow(
                dateString = dateString,
                timeString = timeString,
                timeGivenPerQuestionString = "${test.minutesPerQuestion} minutes"
            )
            if (isExpanded) {
                ExamerCardExpandedContent(
                    description = test.description,
                    totalNumberOfQuestions = test.totalNumberOfQuestions,
                    onTakeTestButtonClick = onTakeTestButtonClick ?: {}
                )
            }
        }
    }
}

@Composable
fun StatusRow(
    testStatus: Status,
    modifier: Modifier = Modifier,
    isDarkModeEnabled: Boolean = !MaterialTheme.colors.isLight,
) {
    val testCardColors = getExamerTestCardColorsForTheme(isDarkModeEnabled = isDarkModeEnabled)
    val backgroundColor = when (testStatus) {
        Status.OPEN -> testCardColors.statusColors.open
        Status.SCHEDULED -> testCardColors.statusColors.scheduled
        Status.MISSED -> testCardColors.statusColors.missed
        Status.COMPLETED -> testCardColors.statusColors.completed
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(backgroundColor)
        )
        Text(
            text = testStatus.toString().lowercase().capitalize(Locale.current),
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
private fun ExamerCardHeader(
    title: String,
    isExpanded: Boolean,
    onExpandButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            maxLines = 1
        )
        IconButton(onClick = onExpandButtonClick) {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ArrowDropUp
                else Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }
    }
}


@Composable
private fun ExamerCardMetadataRow(
    dateString: String,
    timeString: String,
    timeGivenPerQuestionString: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(ContentAlpha.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icons = listOf(Icons.Filled.Event, Icons.Filled.Schedule, Icons.Filled.HourglassTop)
        val text = listOf(dateString, timeString, "$timeGivenPerQuestionString per question")
        icons.zip(text).forEach {
            // apply offset to compensate for the inherent padding of the vector art
            Icon(
                modifier = Modifier.offset(x = (-2).dp),
                imageVector = it.first,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = it.second,
                style = MaterialTheme.typography.caption
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun ExamerCardExpandedContent(
    description: String,
    totalNumberOfQuestions: Int,
    onTakeTestButtonClick: (() -> Unit),
    isDarkModeEnabled: Boolean = !MaterialTheme.colors.isLight
) {
    val examerTestCardColors = getExamerTestCardColorsForTheme(isDarkModeEnabled)
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.label_test_description),
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "${stringResource(id = R.string.label_total_number_of_questions)}: $totalNumberOfQuestions",
            style = MaterialTheme.typography.subtitle2
        )
        Text(text = description)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onTakeTestButtonClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = examerTestCardColors.takeTestButtonColor
            )
        ) {
            Icon(
                imageVector = Icons.Filled.PlayCircleFilled,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.button_label_start_test),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun getExamerTestCardColorsForTheme(isDarkModeEnabled: Boolean) =
    if (isDarkModeEnabled) ExamerTestCardColors.darkExamerTestCardColors
    else ExamerTestCardColors.lightExamerTestCardColors