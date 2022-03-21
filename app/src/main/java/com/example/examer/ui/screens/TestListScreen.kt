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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.examer.data.domain.TestDetails
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.launch

/**
 * A stateful composable that is used to display a list of [TestDetails]
 * as expandable cards.This composable adds support for swipe-to-refresh
 * and a 'scroll-to-top' fab that appears after the first two items are
 * pushed off of the screen while scrolling. It manages the expanded
 * states and also the onClick action, which will toggle between
 * expanded/not expanded states.
 *
 * @param listHeader the header text of the list.
 * @param testList the list of [TestDetails] to be displayed on
 * the screen.
 * @param swipeRefreshState the state object to be used to control
 * or observe the SwipeRefresh state.
 * @param onRefresh Lambda which is invoked when a swipe to refresh
 * gesture is completed.
 * @param listItem used to specify the appearance of each item in the
 * list. The lambda provides the testDetailsItem,isExpanded,onExpandedButtonClick,
 * onClick (which has a default behaviour of expanding the card) and is24hourFormat.
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun TestListScreen(
    listHeader: String,
    testList: List<TestDetails>,
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    listItem: @Composable (
        testDetailsItem: TestDetails,
        isExpanded: Boolean,
        onExpandButtonClick: () -> Unit,
        onClick: () -> Unit,
        is24hourFormat: Boolean,
    ) -> Unit,
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
    val expandedState = remember(testList) {
        mutableStateMapOf<String, Boolean>().apply {
            testList.forEach { this[it.id] = false }
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val insetPaddingValues = rememberInsetsPaddingValues(
        insets = LocalWindowInsets.current.navigationBars
    )
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
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = insetPaddingValues
                ) {
                    item {
                        Text(
                            text = listHeader,
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    items(testList, key = { it.id }) {
                        listItem(
                            testDetailsItem = it,
                            isExpanded = expandedState[it.id] == true,
                            onExpandButtonClick = {
                                expandedState[it.id] = !expandedState[it.id]!!
                            },
                            onClick = { expandedState[it.id] = !expandedState[it.id]!! },
                            is24hourFormat = DateFormat.is24HourFormat(context)
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
