package com.yoti.android.cryptocurrencychallenge.data

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException
import java.lang.RuntimeException

@OptIn(ExperimentalCoroutinesApi::class)
class ApiCallExecutorTest {

    private lateinit var sut: ApiCallExecutor

    @Before
    fun setUp() {
        sut = ApiCallExecutor()
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 1
    }

    @Test
    fun `performCall() returns Result success if call executes without exceptions`() = runTest {
        // Given
        val call = { 1000 }

        // when
        val callResult = sut.performCall(call)

        // Then
        assertTrue(callResult.isSuccess)
        assertEquals(1000, callResult.getOrNull())
    }

    @Test
    fun `performCall() fails with InvalidResponseException if response is invalid`() = runTest {
        // Given
        val call = { 1000 }
        val validate = { _: Int -> false }

        // when
        val callResult = sut.performCall(call, validate)

        // Then
        assertTrue(callResult.isFailure)
        assertTrue(callResult.exceptionOrNull() is NetworkException.InvalidResponseException)
    }

    @Test
    fun `performCall() fails with ClientException if call fails with client error code`() = runTest {
        // Given
        val mockException: HttpException = mockk()
        every { mockException.code() } returns 404
        every { mockException.message } returns "testMessage"
        val call = { throw mockException }

        // when
        val callResult = sut.performCall(call)

        // Then
        assertTrue(callResult.isFailure)
        assertEquals(NetworkException.ClientException(404, "testMessage"), callResult.exceptionOrNull())
    }

    @Test
    fun `performCall() fails with ServerException if call fails with server error code`() = runTest {
        // Given
        val mockException: HttpException = mockk()
        every { mockException.code() } returns 500
        every { mockException.message } returns "testMessage"
        val call = { throw mockException }

        // when
        val callResult = sut.performCall(call)

        // Then
        assertTrue(callResult.isFailure)
        assertEquals(NetworkException.ServerException(500, "testMessage"), callResult.exceptionOrNull())
    }

    @Test
    fun `performCall() fails with NoNetworkException if call fails IOException`() = runTest {
        // Given
        val call = { throw IOException() }

        // when
        val callResult = sut.performCall(call)

        // Then
        assertTrue(callResult.isFailure)
        assertTrue(callResult.exceptionOrNull() is NetworkException.NoNetworkException)
    }

    @Test
    fun `performCall() fails with NoNetworkException if call fails unknown exception`() = runTest {
        // Given
        val call = { throw RuntimeException("Unknown exception") }

        // when
        val callResult = sut.performCall(call)

        // Then
        assertTrue(callResult.isFailure)
        assertTrue(callResult.exceptionOrNull() is NetworkException.UnknownNetworkException)
    }
}