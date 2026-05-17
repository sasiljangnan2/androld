package com.example.lab2271407

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val itemDao: ItemDao) {
    val allItems: Flow<List<ItemEntity>> = itemDao.getAllItems()

    suspend fun insert(item: ItemEntity) {
        withContext(Dispatchers.IO) {
            itemDao.insertItem(item)
        }
    }
}
