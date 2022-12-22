package com.example.data.mapper

import com.example.domain.model.Movie
import com.example.domain.model.Search
import com.example.movieapp.data.entity.SearchResp

fun SearchResp.toSearch() = Search(
    movieList = searches.map {
        Movie(
            id = it.imdbId,
            title = it.title,
            year = it.year,
            type = it.type,
            poster = it.poster
        )
    }.toMutableList(),
    totalCnt = totalCnt,
    curPage = 0

)

fun SearchResp.toEmptySearch() = Search(
    movieList = emptyList<Movie>().toMutableList(),
    totalCnt = 0,
    curPage = 0
)