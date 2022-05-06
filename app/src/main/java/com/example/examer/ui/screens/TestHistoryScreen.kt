package com.example.examer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.TestResult
import com.example.examer.data.domain.getDateStringAndTimeString
import com.example.examer.ui.components.examerTestCard.ExamerCardMetadataRow
import com.example.examer.ui.components.examerTestCard.StatusRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun TestHistoryScreen(
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    testResultsMap: Map<TestDetails, TestResult>
) {
    val listHeader = stringResource(id = R.string.label_test_history)
    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = listHeader,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(testResultsMap.keys.toList()) { testDetailsItem ->
                TestResultCard(
                    modifier = Modifier.fillMaxWidth(),
                    test = testDetailsItem,
                    marksObtained = testResultsMap.getValue(testDetailsItem).marksObtained,
                    maxMarks = testResultsMap.getValue(testDetailsItem).maximumMarks
                )
            }
        }
    }
}

@Composable
private fun TestResultCard(
    test: TestDetails,
    marksObtained: Int,
    maxMarks: Int,
    modifier: Modifier = Modifier,
    is24HourTimeFormat: Boolean = false,
) {
    val (dateString, timeString) = test.getDateStringAndTimeString(is24hourFormat = is24HourTimeFormat)
    val resources = LocalContext.current.resources
    Card {
        Row(modifier = modifier) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = test.title,
                    style = MaterialTheme.typography.h5,
                    maxLines = 1
                )
                Text(text = test.language, style = MaterialTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(8.dp))
                StatusRow(testStatus = test.testStatus)
                Spacer(modifier = Modifier.height(8.dp))
                ExamerCardMetadataRow(
                    dateString = dateString,
                    timeString = timeString,
                    testDurationInMinutes = "${test.testDurationInMinutes} ${resources.getString(R.string.label_minutes)}"
                )
            }
            Text(
                modifier = Modifier
                    .weight(0.2f)
                    .align(Alignment.CenterVertically),
                text = "$marksObtained/$maxMarks",
                style = MaterialTheme.typography.h5
            )
        }
    }
}

