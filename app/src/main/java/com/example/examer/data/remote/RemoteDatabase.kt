package com.example.examer.data.remote

import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails

interface RemoteDatabase {
    suspend fun fetchScheduledTestListForUser(user: ExamerUser): List<TestDetails>
    suspend fun fetchPreviousTestListForUser(user: ExamerUser): List<TestDetails>
}