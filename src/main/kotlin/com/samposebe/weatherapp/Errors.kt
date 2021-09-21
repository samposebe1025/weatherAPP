package com.samposebe.weatherapp

enum class Errors(val err: String){
    PARAMS("ERROR. You must specify the coordinates (-с). Specify the path to the file (-f) if necessary."),
    UNSUPPORTED("ERROR. Unsupported keys specified"),
    COORDINATES("ERROR. Coordinate format is not correct"),
    LATITUDE("ERROR. Enter correct latitude"),
    LONGITUDE("ERROR. Enter correct longitude"),
}