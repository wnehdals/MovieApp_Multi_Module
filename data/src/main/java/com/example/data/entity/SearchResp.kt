package com.example.movieapp.data.entity

import com.google.gson.annotations.SerializedName

data class SearchResp(
    @SerializedName("Search")
    val searches: MutableList<MovieEntity>,

    @SerializedName("totalResults")
    val totalCnt: Int,

    @SerializedName("Response")
    val response: Boolean,

    )
