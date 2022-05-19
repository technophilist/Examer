package com.example.examer.ui.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.examer.R
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

data class VectorArtCard(
    @DrawableRes val id: Int,
    val title: String,
    val description: String,
    val imageDescription: String
)

val defaultExamerVectorArtCards: List<VectorArtCard>
    @Composable
    get() = listOf(
        VectorArtCard(
            R.drawable.welcome_to_examer_vector_art,
            title = stringResource(id = R.string.label_take_your_test_online_title),
            description = stringResource(id = R.string.label_take_your_test_online_description),
            imageDescription = stringResource(id = R.string.label_take_your_test_online_description)
        ),
        VectorArtCard(
            id = R.drawable.scheduled_tests_automatically_appear,
            title = stringResource(id = R.string.label_taking_test_have_never_been_easier_title),
            description = stringResource(id = R.string.label_taking_test_have_never_been_easier_description),
            imageDescription = stringResource(id = R.string.label_taking_test_have_never_been_easier_description)
        ),
        // TODO This is not be applicable to devices running Android P and below
        VectorArtCard(
            id = R.drawable.dark_mode_vector_art,
            title = stringResource(id = R.string.label_reduce_eye_strain_with_dark_mode_title),
            description = stringResource(id = R.string.label_reduce_eye_strain_with_dark_mode_description),
            imageDescription = stringResource(id = R.string.label_reduce_eye_strain_with_dark_mode_description)
        )
    )

/**
 * A stateless implementation of Welcome Screen.
 *
 * @param onCreateAccountButtonClick the action to perform when create
 * account button is pressed.
 * @param onLoginButtonClick the action to perform when login button
 * is pressed.
 * @param vectorArtCards optional param that defines the cards that
 * are to be displayed as a carousel. Defaults to
 * [defaultExamerVectorArtCards] which contains a list of default
 * vector art cards.
 */
@ExperimentalPagerApi
@Composable
fun WelcomeScreen(
    onCreateAccountButtonClick: () -> Unit,
    onLoginButtonClick: () -> Unit,
    vectorArtCards: List<VectorArtCard> = defaultExamerVectorArtCards
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(8.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = stringResource(id = R.string.label_welcome_to_examer),
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle1,
        )
        VectorArtCarousel(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(bottom = 24.dp),
            vectorArtCards = vectorArtCards
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = onCreateAccountButtonClick,
            content = { Text(text = stringResource(id = R.string.button_label_create_account)) },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        )
        TextButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp),
            onClick = onLoginButtonClick
        ) {
            Text(
                text = stringResource(id = R.string.button_label_login),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun VectorArtCarousel(modifier: Modifier = Modifier, vectorArtCards: List<VectorArtCard>) {
    val pagerState = rememberPagerState(
        pageCount = vectorArtCards.size,
        infiniteLoop = true,
        initialOffscreenLimit = 2,
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            state = pagerState,
        ) { page ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    modifier = Modifier
                        .height(350.dp)
                        .fillMaxWidth(),
                    painter = painterResource(id = vectorArtCards[page].id),
                    contentDescription = vectorArtCards[page].imageDescription
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    text = vectorArtCards[page].title,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .padding(start = 32.dp, end = 32.dp)
                        .fillMaxWidth(),
                    text = vectorArtCards[page].description,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center
                )
            }
        }
        HorizontalPagerIndicator(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            pagerState = pagerState
        )
    }
}






