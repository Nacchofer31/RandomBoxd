package com.nacchofer31.randomboxd.dependencies

import androidx.lifecycle.ViewModel

class MyViewModel(
    private  val repository: MyRepository
) : ViewModel() {
    fun getHelloWorldString(): String{
        return repository.helloWorld()
    }
}