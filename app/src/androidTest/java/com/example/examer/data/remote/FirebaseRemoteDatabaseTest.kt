package com.example.examer.data.remote

import android.util.Log
import com.example.examer.data.domain.ExamerUser
import com.example.examer.di.DispatcherProvider
import com.example.examer.di.StandardDispatchersProvider
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.Exception
import java.lang.IllegalArgumentException

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
    fun fetchTestListTest_validUser_isSuccessfullyFetched() {
        // given a valid user
        // it must be possible to fetch the list of all tests related
        // to that particular user without an exception.
        runBlocking { remoteDatabase.fetchTestListForUser(currentUser) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun fetchTestList_invalidUser_exceptionIsRaised() {
        // given - Invalid user
        val invalidUser = ExamerUser("test", "", "")
        // when - trying to get the test list for the invalid user.
        runBlocking { remoteDatabase.fetchTestListForUser(invalidUser) }
        // then - the test must throw an instance of IllegalArguement exception
    }


}