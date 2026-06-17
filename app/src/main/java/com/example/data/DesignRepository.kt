package com.example.data

import kotlinx.coroutines.flow.Flow

class DesignRepository(private val dao: DesignDao) {
    val allDesigns: Flow<List<SavedDesign>> = dao.getAllDesigns()

    suspend fun insert(design: SavedDesign) {
        dao.insertDesign(design)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteDesignById(id)
    }
}
