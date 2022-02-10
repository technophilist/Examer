package com.example.examer.data

import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails
import com.example.examer.data.remote.RemoteDatabase

interface Repository {
    suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails>
}

class ExamerRepository(private val remoteDatabase: RemoteDatabase) : Repository {
    override suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails> =
        remoteDatabase.fetchTestListForUser(user)
}