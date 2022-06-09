package com.yoti.android.cryptocurrencychallenge.data

import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ApiCallExecutor @Inject constructor() {

    suspend fun <T> performCall(call: suspend () -> T, validate: ((T) -> Boolean)? = null): Result<T> {
        return try {
            val response = call()
            if (validate != null && !validate(response)) {
                Result.failure(NetworkException.InvalidResponseException())
            } else {
                Result.success(response)
            }
        } catch (exception: HttpException) {
            Log.d(TAG, "HttpException encountered during api call: $exception")
            val networkException = when (exception.code()) {
                in 400..499 -> NetworkException.ClientException(exception.code(), exception.message)
                in 500..599 -> NetworkException.ServerException(exception.code(), exception.message)
                else -> NetworkException.UnknownNetworkException
            }
            Result.failure(networkException)
        } catch (exception: IOException) {
            Log.d(TAG, "IOException encountered during api call: $exception")
            Result.failure(NetworkException.NoNetworkException)
        } catch (exception: Exception) {
            Log.d(TAG, "Unknown exception encountered during api call: $exception")
            Result.failure(NetworkException.UnknownNetworkException)
        }
    }

    companion object {
        private const val TAG = "ApiCallExecutor"
    }
}