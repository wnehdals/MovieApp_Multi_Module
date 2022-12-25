package com.example.domain.model

import com.google.gson.annotations.SerializedName

data class SearchResp(
    @SerializedName("Search")
    val searches: MutableList<Movie> = mutableListOf(),

    @SerializedName("totalResults")
    val totalCnt: Int = 0,

    @SerializedName("Response")
    val response: Boolean = false


    ) {
    companion object {
        val EMPTY = SearchResp(mutableListOf(), 0, false)
    }
}