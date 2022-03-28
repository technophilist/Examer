package com.example.examer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserAnswersEntityDao {
    @Query("select * from UserAnswersEntity")
    suspend fun getAllUserAnswersEntities(): List<UserAnswersEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserAnswersEntityList(entityList: List<UserAnswersEntity>)
}