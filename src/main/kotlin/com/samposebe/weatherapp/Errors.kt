package com.samposebe.weatherapp

enum class Errors(val err: String){
    PARAMS("ERROR. You must specify the coordinates (-с). Specify the path to the file (-f) if necessary."),
    UNSUPPORTED("ERROR. Unsupported keys specified"),
    COORDINATES("ERROR. Coordinate format is not correct"),
    LATITUDE("ERROR. Enter correct latitude"),
    LONGITUDE("ERROR. Enter correct longitude"),
    KEY("ERROR. Key to API is missing"),
    TIMEOUT("ERROR. Timed out waiting for a response from the server"),
    CONNECTION("ERROR. Сommunication problems"),
    RESPONSE("ERROR. Incorrect server response"),
    FILE("ERROR. Invalid file path"),
    ERROR("ERROR. Sorry but we don't know what happened")
}