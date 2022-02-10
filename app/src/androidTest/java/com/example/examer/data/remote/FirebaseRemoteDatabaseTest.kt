package com.example.examer.data.remote

import com.example.examer.data.domain.ExamerUser
import com.example.examer.di.StandardDispatchersProvider
import kotlinx.coroutines.*
import org.junit.Before
import org.junit.Test

class FirebaseRemoteDatabaseTest {
    // TODO run test on local emulator
    private val testUserID = "test_user_firebase"
    lateinit var remoteDatabase: RemoteDatabase
    lateinit var currentUser: ExamerUser

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        remoteDatabase = FirebaseRemoteDatabase(StandardDispatchersProvider(io = Dispatchers.Main))
        currentUser = ExamerUser(id = testUserID, "testUserName", "testUserEmail")
    }

    @Test
    fun fetchTestListTest_registered_isSuccessfullyFetched() {
        // given a registered user
        // it must be possible to fetch the list of all tests related
        // to that particular user without an exception.
        runBlocking { remoteDatabase.fetchScheduledTestListForUser(currentUser) }
    }

}