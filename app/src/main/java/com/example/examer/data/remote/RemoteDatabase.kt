package com.example.examer.data.remote

import com.example.examer.data.domain.ExamerUser
import com.example.examer.data.domain.TestDetails

interface RemoteDatabase {
    suspend fun fetchTestListForUser(user:ExamerUser): List<TestDetails>
}