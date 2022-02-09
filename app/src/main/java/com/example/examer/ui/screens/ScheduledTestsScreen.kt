package com.example.examer.ui.screens

import android.text.format.DateFormat
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.example.examer.data.domain.TestDetails
import com.example.examer.ui.components.examerTestCard.DefaultExamerExpandableTestCard
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ScheduledTestsScreen(
    tests: List<TestDetails>,
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val isScrollToTopButtonVisible = remember(lazyListState.firstVisibleItemIndex) {
        lazyListState.firstVisibleItemIndex > 2
    }
    // Since the test list is fetched asynchronously, initial value
    // of the tests param will be empty. If key argument is not
    // specified for the remember block, the map would always be
    // empty because the remember block is not re-executed. By
    // executing the remember block whenever the list changes
    // we ensure that the map will always contain the updated
    // values.
    val expandedState = remember(tests) {
        mutableStateMapOf<String, Boolean>().apply {
            tests.forEach { this[it.id] = false }
        }
    }
    val coroutineScope = rememberCoroutineScope()
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(id = R.string.label_upcoming_tests),
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    items(tests, key = { it.id }) {
                        DefaultExamerExpandableTestCard(
                            test = it,
                            isExpanded = expandedState[it.id] == true,
                            onExpandButtonClick = {
                                expandedState[it.id] = !expandedState[it.id]!!
                            },
                            onClick = {
                                expandedState[it.id] = !expandedState[it.id]!!
                            },
                            is24HourTimeFormat = DateFormat.is24HourFormat(context),
                            onTakeTestButtonClick = {} // TODO hoist
                        )
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
}
