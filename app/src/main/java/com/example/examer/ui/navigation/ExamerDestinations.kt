package com.example.examer.ui.navigation

import android.net.Uri
import com.example.examer.data.domain.ExamerAudioFile
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.domain.WorkBook
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class ExamerDestinations(val route: String) {
    object ScheduledTestsScreen :
        ExamerDestinations("ExamerDestinations.ScheduledTestsScreen")

    object LoggedInScreen : ExamerDestinations("ExamerDestinations.LoggedInRoute")
    object TestHistoryScreen : ExamerDestinations("ExamerDestinations.TestHistoryScreen")
    object ProfileScreen : ExamerDestinations("ExamerDestinations.ProfileScreen")

    /**
     * Note: The trailing '/' is required even though the docs don't
     * mention it. Removing it will cause part of the second argument
     * to be cut off.
     */
    object TakeTestScreen :
        ExamerDestinations("ExamerDestinations.TakeTestScreen/{testDetails}/{workBookList}/") {
        fun buildRoute(
            testDetails: TestDetails,
            workBookList: List<WorkBook>
        ): String {
            val testDetailsJsonString = Json.encodeToString(testDetails)
            // encode all URL's in order to pass the object.
            // since compose' navigation system is based on the URL system,
            // passing a serialized uri object containing string of url will
            // cause an exception because a url (of the audio file), is nested
            // inside another url (the destination route).
            val encodedUrlWorkBookList = workBookList.map {
                // encode the url using URL encoder
                val encodedUrl = URLEncoder.encode(
                    it.audioFile.localAudioFileUri.toString(),
                    StandardCharsets.UTF_8.toString()
                )
                // create a new instance of ExamerAudioFile and use the
                // newly encoded url
                val audioFileWithEncodedUrl = ExamerAudioFile(
                    Uri.parse(encodedUrl),
                    it.audioFile.numberOfRepeatsAllowedForAudioFile
                )
                // use the new audio file class
                it.copy(audioFile = audioFileWithEncodedUrl)
            }
            val workBookListJsonString = Json.encodeToString(encodedUrlWorkBookList)
            return "ExamerDestinations.TakeTestScreen/$testDetailsJsonString/$workBookListJsonString/"
        }
    }
}