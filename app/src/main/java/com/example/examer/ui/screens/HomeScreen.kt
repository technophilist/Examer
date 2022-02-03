package com.example.examer.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun HomeScreen(tests: List<TestDetails>) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val isScrollToTopButtonVisible = remember(lazyListState.firstVisibleItemIndex) {
        lazyListState.firstVisibleItemIndex > 2
    }
    val expandedState = remember {
        val map = mutableStateMapOf<String, Boolean>()
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
            LazyColumn(
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
                        onExpandButtonClick = { expandedState[it.id] = !expandedState[it.id]!! },
                        onClick = { expandedState[it.id] = !expandedState[it.id]!! },
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
