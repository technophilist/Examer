package com.example.examer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

// TODO Add docs
@Composable
fun NonClickableTestInfoChip(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50f),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

// TODO Add docs (Overloaded function)
@Composable
fun NonClickableTestInfoChip(
    text: String,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    backgroundColor: Color = MaterialTheme.colors.secondary,
) {
    NonClickableTestInfoChip(
        modifier = modifier,
        backgroundColor = backgroundColor
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = textStyle
        )
    }
}