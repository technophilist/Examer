package com.example.examer.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.twotone.ViewList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.example.examer.data.domain.TestDetails
import com.example.examer.ui.components.examerTestCard.DefaultExamerExpandableTestCard
import com.example.examer.ui.components.examerTestCard.ExamerExpandableTestCard
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun TestHistoryScreen(
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    tests: List<TestDetails>,
    onReviewButtonClick: (TestDetails) -> Unit
) {
    val listHeader = stringResource(id = R.string.label_test_history)
    TestListScreen(
        listHeader = listHeader,
        testList = tests,
        swipeRefreshState = swipeRefreshState,
        onRefresh = onRefresh
    ) { testDetailsItem, isExpanded, onExpandButtonClick, onClick, is24hourFormat ->
        ExamerExpandableTestCard(
            test = testDetailsItem,
            isExpanded = isExpanded,
            onExpandButtonClick = onExpandButtonClick,
            onClick = onClick,
            is24HourTimeFormat = is24hourFormat
        ) {
            ExpandedContent(
                description = testDetailsItem.description,
                totalNumberOfQuestions = testDetailsItem.totalNumberOfQuestions,
                onReviewButtonClick = { onReviewButtonClick(testDetailsItem) }
            )
        }
    }
}

@Composable
private fun ExpandedContent(
    description: String,
    totalNumberOfQuestions: Int,
    onReviewButtonClick: (() -> Unit),
) {
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
            onClick = onReviewButtonClick,
        ) {
            Icon(
                imageVector = Icons.Filled.ViewList,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = R.string.button_label_review_test),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
        }
    }
}