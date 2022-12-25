package com.example.movieapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Movie")
data class MovieEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,             // id
    val title: String,          // 영화 제목
    val year: String,           // 시간
    val type: String,           // 종류
    val poster: String,         // 포스터
    val rank: Int               // 순위
)
