package com.nacchofer31.randomboxd.random_film.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserName(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
)
