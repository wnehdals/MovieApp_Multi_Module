package com.example.movieapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Movie")
data class MovieEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val year: String,
    val type: String,
    val poster: String
)
