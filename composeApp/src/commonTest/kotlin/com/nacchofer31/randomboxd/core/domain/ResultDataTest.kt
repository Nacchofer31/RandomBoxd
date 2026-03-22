package com.nacchofer31.randomboxd.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class ResultDataTest {
    private val successResult: ResultData<Int, DataError> = ResultData.Success(42)
    private val errorResult: ResultData<Int, DataError> = ResultData.Error(DataError.Remote.UNKNOWN)

    @Test
    fun `map transforms success data`() {
        val result = successResult.map { it * 2 }
        assertIs<ResultData.Success<Int>>(result)
        assertEquals(84, result.data)
    }

    @Test
    fun `map on error preserves error`() {
        val result = errorResult.map { it * 2 }
        assertIs<ResultData.Error<DataError>>(result)
        assertEquals(DataError.Remote.UNKNOWN, result.error)
    }

    @Test
    fun `onSuccess executes action for success`() {
        var captured: Int? = null
        successResult.onSuccess { captured = it }
        assertEquals(42, captured)
    }

    @Test
    fun `onSuccess does not execute action for error`() {
        var executed = false
        errorResult.onSuccess { executed = true }
        assertNull(null.takeIf { executed })
        assertEquals(false, executed)
    }

    @Test
    fun `onError executes action for error`() {
        var captured: DataError? = null
        errorResult.onError { captured = it }
        assertEquals(DataError.Remote.UNKNOWN, captured)
    }

    @Test
    fun `onError does not execute action for success`() {
        var executed = false
        successResult.onError { executed = true }
        assertEquals(false, executed)
    }

    @Test
    fun `onSuccess returns same instance`() {
        val result = successResult.onSuccess { }
        assertEquals(successResult, result)
    }

    @Test
    fun `onError returns same instance`() {
        val result = errorResult.onError { }
        assertEquals(errorResult, result)
    }

    @Test
    fun `asEmptyDataResult converts success to unit success`() {
        val result = successResult.asEmptyDataResult()
        assertIs<ResultData.Success<Unit>>(result)
    }

    @Test
    fun `asEmptyDataResult preserves error`() {
        val result = errorResult.asEmptyDataResult()
        assertIs<ResultData.Error<DataError>>(result)
        assertEquals(DataError.Remote.UNKNOWN, result.error)
    }
}
