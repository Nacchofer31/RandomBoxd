package com.nacchofer31.randomboxd.utils.http

import io.ktor.http.HttpStatusCode

data class HttpResponseData(
    val content: String,
    val statusCode: HttpStatusCode,
)
