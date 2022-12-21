package com.example.movieapp.data.entity

import com.google.gson.annotations.SerializedName

data class MovieEntity(
    @SerializedName("imdbID")
    val imdbId: String,
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val poster: String
)
