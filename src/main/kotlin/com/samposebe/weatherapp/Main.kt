@file:JvmName("Main")

package com.samposebe.weatherapp

import com.samposebe.weatherapp.data.WeatherResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.*
import java.io.FileWriter
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


const val LATITUDE_MAX =  90.0
const val LATITUDE_MIN = -90.0

const val LONGITUDE_MAX =  180.0
const val LONGITUDE_MIN = -180.0

val moshi: Moshi = Moshi.Builder().build()
val adapter: JsonAdapter<WeatherResponse> = moshi.adapter(WeatherResponse::class.java)

var error:String? = null

fun getopt(args: Array<String>): Map<String, String>
{
    var last = ""
    return args.fold(mutableMapOf()) {
            acc: MutableMap<String, String>, s: String ->
                acc.apply {
                if (s.startsWith('-')) {
                    when(s){
                        "-c", "-f" -> {
                            this[s] = ""
                            last = s }
                        else -> { //val<0
                            this[last] = this[last].plus(s)
                        }
                    }

                }
                else
                    this[last] = this[last].plus(s)
        }
    }
}


fun getTemp(lat: String,
        lon: String,
        appID: String):String? {

    var result: String? = Errors.RESPONSE.err

    val request = Request.Builder()
        .url("http://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$appID")
        .build()

    val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(50, TimeUnit.MILLISECONDS)
        .build()

    try{
        client.newCall(request).execute().use{ request ->
            //if (!request.isSuccessful) throw IOException("Unexpected code $response")
            val a = request.body
            if (a != null){
                val responseString = a.string()
                val weatherResponse = adapter.fromJson(responseString)
                if (weatherResponse != null) {
                    if (weatherResponse.main != null) {
                        if (weatherResponse.main.temp != null) {
                            val temp = (weatherResponse.main.temp - 273.15).toInt().toString()
                            result = temp//"Temperature = $temp °С"
                        }
                    }
                }
            }
        }
    }catch (e: IOException){
        result = when (e){
            is SocketTimeoutException -> Errors.TIMEOUT.err
            else -> Errors.CONNECTION.err
        }
    }

    return result
}

fun main(args: Array<String>){
    val params = getopt(args)
    //check params

    var temp: String? = null

    if( !params.containsKey("-c") )
        error = Errors.PARAMS.err
    else {
        if ( (params.size == 2 && !params.containsKey("-f")) ||
            (params.size > 2)
        )
            error = Errors.UNSUPPORTED.err
    }

    //read data
    val coordinates = params["-c"]?.split(",")?.map{ it -> it.trim()}
    val latitude:Double?
    val longitude:Double?

    if(coordinates != null){
        if(coordinates.size ==2){
            latitude = coordinates[0].toDoubleOrNull()
            longitude = coordinates[1].toDoubleOrNull()
            if(latitude == null || longitude == null)
                error = Errors.COORDINATES.err
            else {
                if (latitude < LATITUDE_MIN || latitude > LATITUDE_MAX )
                    error = Errors.LATITUDE.err
                else if (longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX )
                    error = Errors.LONGITUDE.err
                else{
                    temp = getTemp(coordinates[0],coordinates[1], System.getenv("WEATHER_API_KEY"))
                }
            }
        } else{
            error = Errors.COORDINATES.err
        }
    } else {
        error = Errors.COORDINATES.err
    }

    var result = ""
    if(temp != null)
        result = temp
    else if (error != null)
        result = error.toString()
    else
        result = Errors.ERROR.err
    println(result)

    try {
        val fileWriter = FileWriter(params["-f"].toString())
        fileWriter.write(result)
        fileWriter.close()
    } catch (exception: Exception) {
        println(Errors.FILE.err)
    }

}