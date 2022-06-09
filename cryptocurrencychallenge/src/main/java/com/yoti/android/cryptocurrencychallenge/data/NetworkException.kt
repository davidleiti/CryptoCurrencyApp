package com.yoti.android.cryptocurrencychallenge.data

sealed class NetworkException : RuntimeException() {
    data class ClientException(val errorCode: Int, val errorMessage: String? = null) : NetworkException()
    data class ServerException(val errorCode: Int, val errorMessage: String? = null) : NetworkException()
    data class InvalidResponseException(val errorMessage: String? = null) : NetworkException()
    object NoNetworkException : NetworkException()
    object UnknownNetworkException : NetworkException()
}