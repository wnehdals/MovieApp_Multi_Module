package com.example.domain.model

import com.google.gson.annotations.SerializedName

data class SearchResp(
    @SerializedName("Search")
    val searches: MutableList<Movie>,

    @SerializedName("totalResults")
    val totalCnt: Int,

    @SerializedName("Response")
    val response: Boolean


    ) {
    companion object {
        val EMPTY = SearchResp(mutableListOf(), 0, false)
    }
}