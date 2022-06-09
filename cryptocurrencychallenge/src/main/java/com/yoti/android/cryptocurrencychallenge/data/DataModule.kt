package com.yoti.android.cryptocurrencychallenge.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext appContext: Context): CryptoDatabase {
            return Room.databaseBuilder(appContext, CryptoDatabase::class.java, "crypto_db").build()
        }

        @Provides
        fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }

        @Provides
        fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        @Provides
        @Singleton
        fun provideCoincapService(): CoincapService {
            return Retrofit.Builder()
                .baseUrl(CoincapService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CoincapService::class.java)
        }
    }
}