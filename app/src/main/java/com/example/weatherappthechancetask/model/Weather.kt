package com.example.weatherappthechancetask.model

data class Weather(
    val coord : Coord? = null,
    val weather: List<WeatherItem>? = null,
    val main: Main? = null,
    val visibility: String? = null,
    val wind: Wind? = null,
    val clouds : Cloud? = null
)