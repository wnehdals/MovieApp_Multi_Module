package com.example.domain.model

data class Search(
    val movieList: MutableList<Movie>,
    val totalCnt: Int,
    var curPage: Int
)
