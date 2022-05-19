package com.example.examer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examer.R
import com.example.examer.data.domain.IndexOfChosenOption
import com.example.examer.data.domain.MultiChoiceQuestion
import com.example.examer.data.domain.UserAnswers
import com.example.examer.ui.components.MultiChoiceQuestionCard
import com.google.accompanist.insets.navigationBarsHeight

enum class ButtonTextValue {
    NEXT_WORKBOOK, FINISH_TEST
}

@Composable
fun WorkBookScreen(
    questionList: List<MultiChoiceQuestion>,
    onFooterButtonClick: (answersMap: Map<MultiChoiceQuestion, IndexOfChosenOption>) -> Unit,
    buttonTextValue: ButtonTextValue = ButtonTextValue.NEXT_WORKBOOK
) {
    // a map that stores the currently selected item associated
    // with a MultiChoiceQuestion object. The key is the
    // MultiChoiceQuestion obj and the value is the index of the
    // currently selected item of that particular question.
    val currentlySelectedIndexMap = remember { mutableStateMapOf<MultiChoiceQuestion, Int>() }
    var isFooterButtonEnabled by remember { mutableStateOf(false) }
    val resources = LocalContext.current.resources
    val (footerButtonText, footerButtonIcon) = remember(buttonTextValue) {
        when (buttonTextValue) {
            ButtonTextValue.NEXT_WORKBOOK -> {
                Pair(
                    resources.getString(R.string.button_label_next_workbook),
                    Icons.Filled.NavigateNext
                )
            }
            ButtonTextValue.FINISH_TEST -> {
                Pair(
                    resources.getString(R.string.button_label_finish_test),
                    Icons.Filled.NavigateNext
                )
            }
        }
    }
    val footer = @Composable {
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier.align(Alignment.End),
                enabled = isFooterButtonEnabled,
                onClick = {
                    val transformedMap: Map<MultiChoiceQuestion, IndexOfChosenOption> =
                        currentlySelectedIndexMap.mapValues {
                            IndexOfChosenOption(it.value)
                        }
                    onFooterButtonClick(transformedMap)
                },
                content = {
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = footerButtonText
                    )
                    Icon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        imageVector = footerButtonIcon,
                        contentDescription = null
                    )
                }
            )
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        itemsIndexed(questionList) { index, item ->
            MultiChoiceQuestionCard(
                modifier = Modifier.fillMaxWidth(),
                questionNumber = index + 1,
                question = item.question,
                options = item.options,
                mark = item.mark,
                currentlySelectedIndex = currentlySelectedIndexMap.getOrPut(item) { -1 },
                onOptionClick = { indexOfOption, _, _ ->
                    currentlySelectedIndexMap[item] = indexOfOption
                    // enable the footer button only when the user has
                    // chosen an answer for all the questions.
                    if (currentlySelectedIndexMap.none { it.value == -1 }) {
                        isFooterButtonEnabled = true
                    }
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
        }
        item {
            Spacer(modifier = Modifier.size(8.dp))
            footer()
            Spacer(
                modifier = Modifier
                    .navigationBarsHeight()
                    .padding(bottom = 8.dp)
            )
        }
    }
}
