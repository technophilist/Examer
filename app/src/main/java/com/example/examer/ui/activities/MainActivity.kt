package com.example.examer.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import coil.annotation.ExperimentalCoilApi
import com.example.examer.di.AppContainer
import com.example.examer.di.ExamerApplication
import com.example.examer.ui.screens.ExamerApp
import com.example.examer.ui.theme.ExamerTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.pager.ExperimentalPagerApi

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    @ExperimentalCoilApi
    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = (application as ExamerApplication).appContainer
        setContent {
            ExamerTheme {
                ProvideWindowInsets {
                    Surface(
                        color = MaterialTheme.colors.background,
                        content = { ExamerApp(appContainer) }
                    )
                }
            }
        }
    }
}

