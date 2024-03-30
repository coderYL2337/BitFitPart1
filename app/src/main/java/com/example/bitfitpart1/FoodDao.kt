package com.example.bitfitpart1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.codepath.articlesearch.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM food_table")
    fun getAll(): Flow<List<FoodEntity>>

    @Insert
   // fun insertAll(foodList: List<FoodEntity>)
    fun insert(food:FoodEntity)

    @Query("DELETE FROM food_table")
    fun deleteAll()

    @Delete
    suspend fun delete(food: FoodEntity)
}