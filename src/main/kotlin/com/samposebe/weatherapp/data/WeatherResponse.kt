package com.samposebe.weatherapp.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse (
    val coord: Coordinates,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Double?,
    val wind: Wind?,
    val clouds: Clouds?,
    val rain: Rain?,
    val snow: Snow?,
    val dt: Int,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Coordinates(
    val lon: Double,
    val lat: Double)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val pressure: Double,
    val humidity: Double,
    val temp_min: Double,
    val temp_max: Double,
    val sea_level: Double,
    val grnd_level: Double)

data class Wind(
    val speed: Double,
    val deg: Double,
    val gust: Double
)

data class Clouds(
    val all: Double)

data class Rain(
    @Json(name = "1h") val h1: Double,
    @Json(name = "3h") val h3: Double
)

data class Snow(
    @Json(name = "1h") val h1: Double,
    @Json(name = "3h") val h3: Double
)


data class Sys(
    val type: Int,
    val id: Int,
    val message: Double,
    val country: String,
    val sunrise: Int,
    val sunset: Int)