@file:JvmName("Main")

package com.samposebe.weatherapp

import com.samposebe.weatherapp.data.WeatherResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.FileWriter
import java.io.IOException

val LATITUDE_MAX =  90.0
val LATITUDE_MIN = -90.0

val LONGITUDE_MAX =  180.0
val LONGITUDE_MIN = -180.0

var client: OkHttpClient = OkHttpClient()
val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

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


val moshi: Moshi = Moshi.Builder().build()
val adapter: JsonAdapter<WeatherResponse> = moshi.adapter(WeatherResponse::class.java)


@Throws(IOException::class)
fun post(url: String, json: String): String? {
    val body: RequestBody = json.toRequestBody(JSON)
    val request: Request = Request.Builder()
        .url(url)
        .post(body)
        .build()
    client.newCall(request).execute().use { response -> return response.body?.string() }
}

fun getTemp(lat: String,
        lon: String,
        appID: String): String? {
    var result: String? = null
    val request = Request.Builder()
        .url("http://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$appID")
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

/*        for ((name, value) in response.headers) {
            println("$name: $value")
        }*/
        val responseString = response.body!!.string()
        if (response.body != null){
            val weatherResponse = adapter.fromJson(responseString)
            if (weatherResponse != null) {
               if (weatherResponse.main.temp != null) {
                   val temp = weatherResponse.main.temp - 273.15
                   result = "Temperature = $temp °С"
                   println(result)
               }
            }
        }
    }
    return result
}

fun main(args: Array<String>){
    val params = getopt(args)
    //check params
    var checkParams = true
    var error:String? = null

    var temp: String? = null

    if( !params.containsKey("-c") )
        error = Errors.PARAMS.toString()
    else {
        if ( (params.size == 2 && !params.containsKey("-f")) ||
            (params.size > 2)
        )
            error = Errors.UNSUPPORTED.toString()
    }

    //check coordinates
    val coordinates = params["-c"]?.split(",")?.map{ it -> it.trim()}
    val latitude:Double?
    val longitude:Double?

    if(coordinates != null){
        if(coordinates?.size ==2){
            latitude = coordinates[0].toDoubleOrNull()
            longitude = coordinates[1].toDoubleOrNull()
            if(latitude == null || longitude == null)
                error = Errors.COORDINATES.toString()
            else {
                if (latitude < LATITUDE_MIN || latitude > LATITUDE_MAX )
                    error = Errors.LATITUDE.toString()
                else if (longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX )
                    error = Errors.LONGITUDE.toString()
                else
                    temp = getTemp(coordinates[0],coordinates[1], System.getenv("WEATHER_API_KEY"))
            }
        } else{
            error = Errors.COORDINATES.toString()
        }
    } else {
        error = Errors.COORDINATES.toString()
    }

    //TODO check file

    if(temp != null) {
        try {
            val fileWriter = FileWriter(params["-f"].toString())
            fileWriter.write(temp)
            fileWriter.close()
        } catch (exception: Exception) {
            println(exception.message)
        }
    }
}