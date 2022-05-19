package com.example.examer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.examer.R

@Composable
fun MultiChoiceQuestionCard(
    questionNumber: Int,
    question: String,
    options: Array<String>,
    mark: Int,
    currentlySelectedIndex: Int,
    onOptionClick: (index: Int, questionNumber: Int, string: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.defaultMinSize(minHeight = 156.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.label_question_number, questionNumber),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Left
                )
                Text(
                    text = stringResource(id = R.string.label_marks, mark),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Left
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
            style = TextStyle.Default.copy(MaterialTheme.colors.onSurface),
            onClick = { onClick() }
        )
    }
}
