package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "saved_designs")
@Serializable
data class SavedDesign(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val data: String, // The text/content encoded in the QR code
    val fgColor: Int, // Color int
    val bgColor: Int,
    val cornerType: String, // "SQUARE", "ROUNDED", etc.
    val logoUri: String?, // Uri to logo if any
    val timestamp: Long = System.currentTimeMillis()
)
