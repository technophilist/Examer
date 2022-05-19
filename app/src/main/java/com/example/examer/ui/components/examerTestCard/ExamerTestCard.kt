package com.example.examer.ui.components.examerTestCard

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.example.examer.data.domain.Status
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.getDateStringAndTimeString

/**
 * This is a default implementation of [ExamerExpandableTestCard]
 * which displays the [ExamerCardExpandedContent] when the card is
 * expanded. [ExamerExpandableTestCard] provides slot for defining
 * the content to be displayed when the card is expanded.
 * @param test an instance of [TestDetails] that the card will use
 * to display the information.
 * @param isExpanded indicates whether the card is expanded.
 * @param onExpandButtonClick callback to be called when the expand
 * button of the card is clicked.
 * @param onTakeTestButtonClick callback to be called when the take
 * test button is clicked.
 * @param modifier the [Modifier] to be applied to the composable.
 * @param onClick callback to be called when the card is clicked
 * @param is24HourTimeFormat indicates whether the time information
 * displayed in the card is in 24 hour format.
 */
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun DefaultExamerExpandableTestCard(
    test: TestDetails,
    isExpanded: Boolean,
    onExpandButtonClick: (() -> Unit),
    onTakeTestButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    is24HourTimeFormat: Boolean = false,
) {
    ExamerExpandableTestCard(
        test = test,
        isExpanded = isExpanded,
        onExpandButtonClick = onExpandButtonClick,
        modifier = modifier,
        onClick = onClick,
        is24HourTimeFormat = is24HourTimeFormat,
    ) {
        ExamerCardExpandedContent(
            description = test.description,
            totalNumberOfQuestions = test.totalNumberOfWorkBooks,
            onTakeTestButtonClick = onTakeTestButtonClick
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun ExamerExpandableTestCard(
    test: TestDetails,
    isExpanded: Boolean,
    onExpandButtonClick: (() -> Unit),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    is24HourTimeFormat: Boolean = false,
    expandedContent: @Composable () -> Unit
) {
    val (dateString, timeString) = test.getDateStringAndTimeString(is24hourFormat = is24HourTimeFormat)
    val resources = LocalContext.current.resources
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
                testDurationInMinutes = "${test.testDurationInMinutes} ${resources.getString(R.string.label_minutes)}"
            )
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(durationMillis = 50)),
                exit = fadeOut(animationSpec = tween(durationMillis = 50)),
                content = { expandedContent() }
            )
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
fun ExamerCardMetadataRow(
    dateString: String,
    timeString: String,
    testDurationInMinutes: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(ContentAlpha.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icons = listOf(Icons.Filled.Event, Icons.Filled.Schedule, Icons.Filled.HourglassTop)
        val text = listOf(dateString, timeString, testDurationInMinutes)
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