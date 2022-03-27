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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examer.R
import com.example.examer.data.domain.MultiChoiceQuestion
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.statusBarsPadding

enum class ButtonTextValue {
    NEXT_WORKBOOK, FINISH_TEST
}

@Composable
fun WorkBookScreen(
    questionList: List<MultiChoiceQuestion>,
    onFooterButtonClick: (Map<MultiChoiceQuestion, Int>) -> Unit,
    buttonTextValue: ButtonTextValue = ButtonTextValue.NEXT_WORKBOOK
) {
    // a map that stores the currently selected item associated
    // with a MultiChoiceQuestion object. The key is the
    // MultiChoiceQuestion obj and the value is the index of the
    // currently selected item of that particular question.
    val currentlySelectedIndexMap = remember {
        mutableStateMapOf<MultiChoiceQuestion, Int>()
    }
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
                onClick = {
                    // convert state map to normal map and pass it as arg
                    onFooterButtonClick(currentlySelectedIndexMap.toMap())
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
            .statusBarsPadding()
            .padding(8.dp)
    ) {
        itemsIndexed(questionList) { index, item ->
            MultiChoiceQuestionCard(
                modifier = Modifier.fillMaxWidth(),
                questionNumber = index + 1,
                question = item.question,
                options = item.options,
                mark = 10,
                currentlySelectedIndex = currentlySelectedIndexMap.getOrPut(item) { -1 },
                onOptionClick = { indexOfOption, _, _ ->
                    currentlySelectedIndexMap[item] = indexOfOption
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
        }
        item {
            footer()
            Spacer(
                modifier = Modifier
                    .navigationBarsHeight()
                    .padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun MultiChoiceQuestionCard(
    questionNumber: Int,
    question: String,
    options: Array<String>,
    mark: Int,
    currentlySelectedIndex: Int,
    onOptionClick: (index: Int, questionNumber: Int, string: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO extract as stateless component
    Card(modifier = modifier.defaultMinSize(minHeight = 156.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.label_question_number, questionNumber),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Left,
                )
                Text(
                    text = stringResource(id = R.string.label_marks, mark),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Left,
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = question,
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(16.dp))
            OptionsRadioGroup(
                options = options,
                onRadioButtonClick = { index, string ->
                    onOptionClick(
                        index,
                        questionNumber,
                        string
                    )
                },
                currentlySelectedIndex = currentlySelectedIndex
            )
        }
    }
}

@Composable
private fun OptionsRadioGroup(
    options: Array<String>,
    onRadioButtonClick: (Int, String) -> Unit,
    currentlySelectedIndex: Int,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        options.forEachIndexed { index, string ->
            RadioButtonWithText(
                text = string,
                selected = currentlySelectedIndex == index,
                onClick = { onRadioButtonClick(index, string) }
            )
        }
    }
}

@Composable
private fun RadioButtonWithText(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        ClickableText(
            text = AnnotatedString(text),
            onClick = { onClick() }
        )
    }
}