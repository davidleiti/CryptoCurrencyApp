package com.yoti.android.cryptocurrencychallenge.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yoti.android.cryptocurrencychallenge.assets.data.local.AssetEntity
import com.yoti.android.cryptocurrencychallenge.assets.data.local.AssetsDao

@Database(entities = [AssetEntity::class], version = 1)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun assetsDao(): AssetsDao
}