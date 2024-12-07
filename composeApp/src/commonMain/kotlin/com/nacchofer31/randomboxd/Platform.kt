package com.nacchofer31.randomboxd

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform