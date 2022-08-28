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
            containerView.findViewById<ImageView>(R.id.ivDescriptionWeatherToday)
        val tvTodayTemp = containerView.findViewById<TextView>(R.id.tvTodayTemp)
        val tvTodayTempDescription =
            containerView.findViewById<TextView>(R.id.tvTodayTempDescription)
        var url: String? = "https://www.bmkg.go.id/asset/img/weather_icon/ID/"
        var am: String? = "-am.png"
        var pm: String? = "-pm.png"
        var replaceString: String? = it.hariini.deskripsi



        tvValueToday.text = it.kota
        tvTodayMorningDescription.text = "Pagi : " + it.hariini.cuacaPagi
        tvTodayAfternoonDescription.text = "Siang : " + it.hariini.cuacaSiang
        tvTodayNightDescription.text = "Malam : " + it.hariini.cuacaMalam
        if (it.hariini.deskripsi == "Cerah") {


            Util.showImageWithGlide123(
                ivDescriptionWeatherToday,
                url + it.hariini.deskripsi + am,
                R.drawable.placeholder_3_2
            )


            //ivDescriptionWeatherToday.setImageDrawable(containerView.context.getDrawable(R.drawable.cerah))
        } else if (it.hariini.deskripsi == "Cerah Berawan") {
            replaceString?.replace(" ", "%20")

            Util.showImageWithGlide123(
                ivDescriptionWeatherToday,
                url + it.hariini.deskripsi + am,
                R.drawable.placeholder_3_2
            )
        } else if (it.hariini.deskripsi == "Berawan") {

            Util.showImageWithGlide123(
                ivDescriptionWeatherToday,
                url + it.hariini.deskripsi + am,
                R.drawable.placeholder_3_2
            )
        } else if (it.hariini.deskripsi == "Hujan Sedang") {
            replaceString?.replace(" ", "%20")
            Util.showImageWithGlide123(
                ivDescriptionWeatherToday,
                url + it.hariini.deskripsi + am,
                R.drawable.placeholder_3_2
            )
        } else if (it.hariini.deskripsi == "Hujan Ringan") {
            replaceString?.replace(" ", "%20")
            Util.showImageWithGlide123(
                ivDescriptionWeatherToday,
                url + it.hariini.deskripsi + am,
                R.drawable.placeholder_3_2
            )
        } else if (it.hariini.deskripsi == "Berawan Tebal") {
            replaceString?.replace(" ", "%20")
            Util.showImageWithGlide123(
                ivDescriptionWeatherToday,
                url + it.hariini.deskripsi + am,
                R.drawable.placeholder_3_2
            )
        } else {
            Util.showImageWithGlide123(
                ivDescriptionWeatherToday,
                url + it.hariini.deskripsi + am,
                R.drawable.placeholder_3_2
            )
        }

        tvTodayTemp.text = it.hariini.suhuMax + " \u2103"
        tvTodayTempDescription.text = it.hariini.deskripsi

        val tvValueAfternoon = containerView.findViewById<TextView>(R.id.tvValueAfternoon)
        val tvAfternoonMorningDescription =
            containerView.findViewById<TextView>(R.id.tvAfternoonMorningDescription)
        val tvAfternoonAfternoonDescription =
            containerView.findViewById<TextView>(R.id.tvAfternoonAfternoonDescription)
        val tvAfternoonNightDescription =
            containerView.findViewById<TextView>(R.id.tvAfternoonNightDescription)
        val ivDescriptionWeatherAfternoon =
            containerView.findViewById<ImageView>(R.id.ivDescriptionWeatherAfternoon)
        val tvAfternoonTemp = containerView.findViewById<TextView>(R.id.tvAfternoonTemp)
        val tvAfternoonTempDescription =
            containerView.findViewById<TextView>(R.id.tvAfternoonTempDescription)
        tvValueAfternoon.text = it.kota
        tvAfternoonMorningDescription.text = "Pagi : " + it.besok.cuacaPagi
        tvAfternoonAfternoonDescription.text = "Siang : " + it.besok.cuacaSiang
        tvAfternoonNightDescription.text = "Malam : " + it.besok.cuacaMalam

        if (it.besok.deskripsi == "Cerah") {
            Util.showImageWithGlide123(
                ivDescriptionWeatherAfternoon,
                url + it.hariini.deskripsi + pm,
                R.drawable.placeholder_3_2
            )
        } else if (it.besok.deskripsi == "Cerah Berawan") {
            replaceString?.replace(" ", "%20")
            Util.showImageWithGlide123(
                ivDescriptionWeatherAfternoon,
                url + it.hariini.deskripsi + pm,
                R.drawable.placeholder_3_2
            )
        } else if (it.besok.deskripsi == "Berawan") {

            Util.showImageWithGlide123(
                ivDescriptionWeatherAfternoon,
                url + it.hariini.deskripsi + pm,
                R.drawable.placeholder_3_2
            )
        } else if (it.besok.deskripsi == "Hujan Sedang") {
            replaceString?.replace(" ", "%20")
            Util.showImageWithGlide123(
                ivDescriptionWeatherAfternoon,
                url + it.hariini.deskripsi + pm,
                R.drawable.placeholder_3_2
            )
        } else if (it.besok.deskripsi == "Hujan Ringan") {
            replaceString?.replace(" ", "%20")
            Util.showImageWithGlide123(
                ivDescriptionWeatherAfternoon,
                url + it.hariini.deskripsi + pm,
                R.drawable.placeholder_3_2
            )
        } else if (it.besok.deskripsi == "Berawan Tebal") {


            CustomLog.d("hasilnyaa", replaceString)
            Util.showImageWithGlide123(
                ivDescriptionWeatherAfternoon,
                url + it.hariini.deskripsi + pm,
                R.drawable.placeholder_3_2
            )
        } else {
            Util.showImageWithGlide123(
                ivDescriptionWeatherAfternoon,
                url + it.hariini.deskripsi + pm,
                R.drawable.placeholder_3_2
            )
        }

        tvAfternoonTemp.text = it.besok.suhuMax + " \u2103"
        tvAfternoonTempDescription.text = it.besok.deskripsi
    }