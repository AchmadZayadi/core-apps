package com.sesolutions.ui.weather.weather

import android.widget.ImageView
import android.widget.TextView
import com.dizcoding.adapterdelegate.bind
import com.dizcoding.adapterdelegate.itemDelegate
import com.manimaran.crash_reporter.CrashReporter.context
import com.sesolutions.R
import com.sesolutions.ui.weather.WeatherDataResponse
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.Util

fun weatherAdapter() = itemDelegate<WeatherDataResponse>(R.layout.item_weather)
    .bind {
        val tvValueToday = containerView.findViewById<TextView>(R.id.tvValueToday)
        val tvTodayMorningDescription =
            containerView.findViewById<TextView>(R.id.tvTodayMorningDescription)
        val tvTodayAfternoonDescription =
            containerView.findViewById<TextView>(R.id.tvTodayAfternoonDescription)
        val tvTodayNightDescription =
            containerView.findViewById<TextView>(R.id.tvTodayNightDescription)
        val ivDescriptionWeatherToday =
            containerView.findViewById<ImageView>(R.id.ivDescriptionWeatherMorning)
        val tvSuhuMinimal = containerView.findViewById<TextView>(R.id.tvSuhuMinimal)
        val tvSuhuMaximal = containerView.findViewById<TextView>(R.id.tvSuhuMaximal)
        val ivDescriptionWeatherAfternoon =
            containerView.findViewById<ImageView>(R.id.ivDescriptionWeatherAfternoon)

        val ivDescriptionWeatherNight =
            containerView.findViewById<ImageView>(R.id.ivDescriptionWeatherNight)
        val url: String? = "https://www.bmkg.go.id/asset/img/weather_icon/ID/"


        val imageMorning = url + it.hariini.cuacaPagi?.replace(" ", "%20") + "-am.png"
        val imageEvening = url + it.hariini.cuacaSiang?.replace(" ", "%20") + "-am.png"
        val imageNight = url + it.hariini.cuacaMalam?.replace(" ", "%20") + "-pm.png"




        tvValueToday.text = it.kota
        tvTodayMorningDescription.text = it.hariini.cuacaPagi
        tvTodayAfternoonDescription.text = it.hariini.cuacaSiang
        tvTodayNightDescription.text = it.hariini.cuacaMalam
        tvSuhuMinimal.text = it.hariini.suhuMin + " \u2103" + " - "
        tvSuhuMaximal.text = it.hariini.suhuMax + " \u2103"
        Util.showImageWithGlide123(
            ivDescriptionWeatherToday,
            imageMorning,
            R.drawable.placeholder_3_2
        )
        Util.showImageWithGlide123(
            ivDescriptionWeatherAfternoon,
            imageEvening,
            R.drawable.placeholder_3_2
        )
        Util.showImageWithGlide123(
            ivDescriptionWeatherNight,
            imageNight,
            R.drawable.placeholder_3_2
        )

        // tvTodayTemp.text = it.hariini.suhuMax + " \u2103"


    }