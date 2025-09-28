package com.nacchofer31.randomboxd.core.data

import com.nacchofer31.randomboxd.core.domain.DataError
import com.nacchofer31.randomboxd.core.domain.ResultData
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): ResultData<T, DataError.Remote> {
    val response =
        try {
            execute()
        } catch (_: SocketTimeoutException) {
            return ResultData.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (_: UnresolvedAddressException) {
            return ResultData.Error(DataError.Remote.NO_INTERNET)
        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return ResultData.Error(DataError.Remote.NO_INTERNET)
        }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): ResultData<T, DataError.Remote> =
    when (response.status.value) {
        in 200..299 -> {
            try {
                ResultData.Success(response.body<T>())
            } catch (_: NoTransformationFoundException) {
                ResultData.Error(DataError.Remote.SERIALIZATION)
            }
        }

        408 -> {
            ResultData.Error(DataError.Remote.REQUEST_TIMEOUT)
        }

        429 -> {
            ResultData.Error(DataError.Remote.TOO_MANY_REQUESTS)
        }

        in 500..599 -> {
            ResultData.Error(DataError.Remote.SERVER)
        }

        else -> {
            ResultData.Error(DataError.Remote.UNKNOWN)
        }
    }
