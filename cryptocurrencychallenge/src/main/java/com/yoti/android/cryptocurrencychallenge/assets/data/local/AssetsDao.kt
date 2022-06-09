package com.yoti.android.cryptocurrencychallenge.assets.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AssetsDao {

    @Query("SELECT * FROM assets")
    abstract fun getAll(): Flow<List<AssetEntity>>

    @Transaction
    open suspend fun replaceAll(entities: List<AssetEntity>) {
        removeAll()
        insertAll(entities)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: List<AssetEntity>)

    @Query("DELETE FROM assets")
    abstract suspend fun removeAll()
}