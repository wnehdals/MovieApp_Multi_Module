package com.example.data.mapper

import com.example.domain.model.Movie
import com.example.movieapp.data.entity.MovieEntity

fun Movie.toMovieEntity() = MovieEntity(
    id = id,
    title = title,
    year = year,
    type = type,
    poster = poster
)
fun MovieEntity.toMovie() = Movie(
    id = id,
    title = title,
    year = year,
    type = type,
    poster = poster,
    isFavorite = true
)