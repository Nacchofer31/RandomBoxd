package com.nacchofer31.randomboxd.core.domain

sealed interface ResultData<out D, out E: Error> {
    data class Success<out D>(val data: D): ResultData<D, Nothing>
    data class Error<out E: com.nacchofer31.randomboxd.core.domain.Error>(val error: E):
        ResultData<Nothing, E>
}

inline fun <T, E: Error, R> ResultData<T, E>.map(map: (T) -> R): ResultData<R, E> {
    return when(this) {
        is ResultData.Error -> ResultData.Error(error)
        is ResultData.Success -> ResultData.Success(map(data))
    }
}

fun <T, E: Error> ResultData<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  }
}

inline fun <T, E: Error> ResultData<T, E>.onSuccess(action: (T) -> Unit): ResultData<T, E> {
    return when(this) {
        is ResultData.Error -> this
        is ResultData.Success -> {
            action(data)
            this
        }
    }
}
inline fun <T, E: Error> ResultData<T, E>.onError(action: (E) -> Unit): ResultData<T, E> {
    return when(this) {
        is ResultData.Error -> {
            action(error)
            this
        }
        is ResultData.Success -> this
    }
}

typealias EmptyResult<E> = ResultData<Unit, E>