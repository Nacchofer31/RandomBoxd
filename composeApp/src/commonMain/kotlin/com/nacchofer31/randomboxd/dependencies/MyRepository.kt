package com.nacchofer31.randomboxd.dependencies

interface MyRepository {
    fun helloWorld() : String
}

class MyRepositoryImpl(
    private val dbClient: DBClient
) : MyRepository {
    override fun helloWorld(): String = "Hello World!"
}