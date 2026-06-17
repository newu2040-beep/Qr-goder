package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DesignDao {
    @Query("SELECT * FROM saved_designs ORDER BY timestamp DESC")
    fun getAllDesigns(): Flow<List<SavedDesign>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDesign(design: SavedDesign)

    @Query("DELETE FROM saved_designs WHERE id = :id")
    suspend fun deleteDesignById(id: Int)
}
