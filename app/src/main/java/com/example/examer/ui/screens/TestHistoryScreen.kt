package com.example.examer.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.example.examer.data.domain.TestDetails
import com.example.examer.ui.components.examerTestCard.ExamerExpandableTestCard
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun TestHistoryScreen(
    tests: List<TestDetails>,
    onReviewButtonClick: (TestDetails) -> Unit
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val isScrollToTopButtonVisible = remember(lazyListState.firstVisibleItemIndex) {
        lazyListState.firstVisibleItemIndex > 2
    }
    val expandedState = remember {
        val map = mutableStateMapOf<Int, Boolean>()
        tests.forEach { map[it.id] = false }
        map
    }
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            LazyColumn(state = lazyListState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text(
                        text = stringResource(id = R.string.label_test_history),
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(tests, key = { it.id }) {
                    ExamerExpandableTestCard(
                        test = it,
                        isExpanded = expandedState[it.id] == true,
                        onExpandButtonClick = { expandedState[it.id] = !expandedState[it.id]!! },
                        onClick = { expandedState[it.id] = !expandedState[it.id]!! },
                        is24HourTimeFormat = DateFormat.is24HourFormat(context)
                    ) {
                        ExpandedContent(
                            description = it.description,
                            totalNumberOfQuestions = it.totalNumberOfQuestions,
                            onReviewButtonClick = { onReviewButtonClick(it) }
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(8.dp),
            visible = isScrollToTopButtonVisible,
            enter = expandIn(expandFrom = Alignment.Center),
            exit = shrinkOut(shrinkTowards = Alignment.Center)
        ) {
            FloatingActionButton(
                onClick = { coroutineScope.launch { lazyListState.animateScrollToItem(0) } },
                content = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropUp,
                        contentDescription = null
                    )
                }
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