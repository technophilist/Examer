package com.example.examer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserAnswersEntity::class], version = 1)
abstract class ExamerDatabase : RoomDatabase() {
    abstract fun userAnswersEntityDao(): UserAnswersEntityDao
}