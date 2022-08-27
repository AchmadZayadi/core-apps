package com.sesolutions.ui.weather

import com.google.gson.annotations.SerializedName

class WeatherResponse {
    @SerializedName("cuaca")
    val cuaca : MutableList<WeatherDataResponse> = mutableListOf()
}

class WeatherDataResponse {
    @SerializedName("kota") val kota: String? = null
    @SerializedName("hariini") val hariini: TodayWeatherResponse = TodayWeatherResponse()
    @SerializedName("besok") val besok: TomorrowWeatherResponse = TomorrowWeatherResponse()
}

class TodayWeatherResponse {
    @SerializedName("cuacaPagi") val cuacaPagi: String? = null
    @SerializedName("cuacaSiang") val cuacaSiang: String? = null
    @SerializedName("cuacaMalam") val cuacaMalam: String? = null
    @SerializedName("deskripsi") val deskripsi: String? = null
    @SerializedName("suhuMin") val suhuMin: String? = null
    @SerializedName("suhuMax") val suhuMax: String? = null
    @SerializedName("kelembabanMin") val kelembabanMin: String? = null
    @SerializedName("kelembabanMax") val kelembabanMax: String? = null
    @SerializedName("kecepatanAngin") val kecepatanAngin: String? = null
}

class TomorrowWeatherResponse {
    @SerializedName("cuacaPagi") val cuacaPagi: String? = null
    @SerializedName("cuacaSiang") val cuacaSiang: String? = null
    @SerializedName("cuacaMalam") val cuacaMalam: String? = null
    @SerializedName("deskripsi") val deskripsi: String? = null
    @SerializedName("arahAngin") val arahAngin: String? = null
    @SerializedName("suhuMin") val suhuMin: String? = null
    @SerializedName("suhuMax") val suhuMax: String? = null
    @SerializedName("kelembabanMin") val kelembabanMin: String? = null
    @SerializedName("kelembabanMax") val kelembabanMax: String? = null
    @SerializedName("kecepatanAngin") val kecepatanAngin: String? = null
}