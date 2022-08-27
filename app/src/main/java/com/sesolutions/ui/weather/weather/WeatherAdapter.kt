package com.sesolutions.ui.weather.weather

import android.widget.ImageView
import android.widget.TextView
import com.dizcoding.adapterdelegate.bind
import com.dizcoding.adapterdelegate.itemDelegate
import com.sesolutions.R
import com.sesolutions.ui.weather.WeatherDataResponse

fun weatherAdapter () = itemDelegate<WeatherDataResponse>(R.layout.item_weather)
    .bind {
        val tvValueToday = containerView.findViewById<TextView>(R.id.tvValueToday)
        val tvTodayMorningDescription = containerView.findViewById<TextView>(R.id.tvTodayMorningDescription)
        val tvTodayAfternoonDescription = containerView.findViewById<TextView>(R.id.tvTodayAfternoonDescription)
        val tvTodayNightDescription = containerView.findViewById<TextView>(R.id.tvTodayNightDescription)
        val ivDescriptionWeatherToday = containerView.findViewById<ImageView>(R.id.ivDescriptionWeatherToday)
        val tvTodayTemp = containerView.findViewById<TextView>(R.id.tvTodayTemp)
        val tvTodayTempDescription = containerView.findViewById<TextView>(R.id.tvTodayTempDescription)
        tvValueToday.text = it.kota
        tvTodayMorningDescription.text = "Pagi : "+it.hariini.cuacaPagi
        tvTodayAfternoonDescription.text = "Siang : "+it.hariini.cuacaSiang
        tvTodayNightDescription.text = "Malam : "+it.hariini.cuacaMalam
        if ( it.hariini.deskripsi == "Cerah"){
            ivDescriptionWeatherToday.setImageDrawable(containerView.context.getDrawable(R.drawable.cerah))
        } else if (it.hariini.deskripsi == "Cerah Berawan"){
            ivDescriptionWeatherToday.setImageDrawable(containerView.context.getDrawable(R.drawable.cerah_berawan))
        } else if (it.hariini.deskripsi == "Berawan"){
            ivDescriptionWeatherToday.setImageDrawable(containerView.context.getDrawable(R.drawable.berawan))
        } else if (it.hariini.deskripsi == "Hujan Sedang"){
            ivDescriptionWeatherToday.setImageDrawable(containerView.context.getDrawable(R.drawable.hujan))
        } else if (it.hariini.deskripsi == "Hujan Ringan"){
            ivDescriptionWeatherToday.setImageDrawable(containerView.context.getDrawable(R.drawable.hujan_ringan))
        } else if (it.hariini.deskripsi == "Berawan Tebal"){
            ivDescriptionWeatherToday.setImageDrawable(containerView.context.getDrawable(R.drawable.berawan))
        }else {
            ivDescriptionWeatherToday.setImageDrawable(containerView.context.getDrawable(R.drawable.cerah))
        }

        tvTodayTemp.text = it.hariini.suhuMax+" \u2103"
        tvTodayTempDescription.text = it.hariini.deskripsi

        val tvValueAfternoon = containerView.findViewById<TextView>(R.id.tvValueAfternoon)
        val tvAfternoonMorningDescription = containerView.findViewById<TextView>(R.id.tvAfternoonMorningDescription)
        val tvAfternoonAfternoonDescription = containerView.findViewById<TextView>(R.id.tvAfternoonAfternoonDescription)
        val tvAfternoonNightDescription = containerView.findViewById<TextView>(R.id.tvAfternoonNightDescription)
        val ivDescriptionWeatherAfternoon = containerView.findViewById<ImageView>(R.id.ivDescriptionWeatherAfternoon)
        val tvAfternoonTemp = containerView.findViewById<TextView>(R.id.tvAfternoonTemp)
        val tvAfternoonTempDescription = containerView.findViewById<TextView>(R.id.tvAfternoonTempDescription)
        tvValueAfternoon.text = it.kota
        tvAfternoonMorningDescription.text = "Pagi : "+it.besok.cuacaPagi
        tvAfternoonAfternoonDescription.text = "Siang : "+it.besok.cuacaSiang
        tvAfternoonNightDescription.text = "Malam : "+it.besok.cuacaMalam

        if ( it.besok.deskripsi == "Cerah"){
            ivDescriptionWeatherAfternoon.setImageDrawable(containerView.context.getDrawable(R.drawable.cerah))
        } else if (it.besok.deskripsi == "Cerah Berawan"){
            ivDescriptionWeatherAfternoon.setImageDrawable(containerView.context.getDrawable(R.drawable.cerah_berawan))
        } else if (it.besok.deskripsi == "Berawan"){
            ivDescriptionWeatherAfternoon.setImageDrawable(containerView.context.getDrawable(R.drawable.berawan))
        } else if (it.besok.deskripsi == "Hujan Sedang"){
            ivDescriptionWeatherAfternoon.setImageDrawable(containerView.context.getDrawable(R.drawable.hujan))
        } else if (it.besok.deskripsi == "Hujan Ringan"){
            ivDescriptionWeatherAfternoon.setImageDrawable(containerView.context.getDrawable(R.drawable.hujan_ringan))
        } else if (it.besok.deskripsi == "Berawan Tebal"){
            ivDescriptionWeatherAfternoon.setImageDrawable(containerView.context.getDrawable(R.drawable.berawan))
        }else {
            ivDescriptionWeatherAfternoon.setImageDrawable(containerView.context.getDrawable(R.drawable.cerah))
        }

        tvAfternoonTemp.text = it.besok.suhuMax+" \u2103"
        tvAfternoonTempDescription.text = it.besok.deskripsi
    }