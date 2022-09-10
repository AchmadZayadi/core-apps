package com.sesolutions.ui.weather

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dizcoding.adapterdelegate.DelegatesAdapter
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.ui.WebViewActivity
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.price.ProvinceActivity
import com.sesolutions.ui.weather.weather.weatherAdapter
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import kotlinx.android.synthetic.main.fragment_price.*
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.android.synthetic.main.fragment_weather.layout_province
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.apache.http.client.methods.HttpPost
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    private lateinit var adapter: DelegatesAdapter<WeatherDataResponse>
    var province: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DelegatesAdapter(
            weatherAdapter()
        )

        ivBack.setOnClickListener {
            onBackPressed()
        }
        toolbar.setBackgroundColor(Color.parseColor("#084B96"))
        tvTitle.text = "Prakiraan Cuaca"

        val today: Date = Calendar.getInstance().getTime()
        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(today)
        tvSumberWeather.text = formattedDate + " - " + "Sumber: "
        tvLinkWebWeather.setOnClickListener {
            val intent = Intent(
                context,
                WebViewActivity::class.java
            )
            intent.putExtra("web", "https://www.bmkg.go.id/")
            intent.putExtra("title", "Panel Cuaca")
            startActivity(intent)
        }

        val rvWeather = view.findViewById<RecyclerView>(R.id.rvWeather)
        rvWeather.layoutManager = LinearLayoutManager(requireContext())
        rvWeather.adapter = adapter

        layout_province.setOnClickListener {
            gotoProvince()
        }

        province = SPref.getInstance().getKecamatan(context)


        if (province.contains("/")) {
            val splittedKecamatan = province.split("/")
            if (splittedKecamatan.isNotEmpty()) {
                province = splittedKecamatan[0].trim()
                tvProvinceWeather.text = splittedKecamatan[0]
            }
        } else {
            tvProvinceWeather.text = province
        }
        callWeatherApi(province.replace(" ", "%20").replace(".", ""))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        province = data?.getStringExtra("kecamatan") ?: ""
        if (province.isNotEmpty() || province.isNotBlank()) {
            tvProvinceWeather.text = province
            callWeatherApi(province.replace(" ", "%20").replace(".", " "))
        }
    }

    fun gotoProvince() {
        val intent = Intent(context, ProvinceActivity::class.java)
        intent.putExtra("name", province)
        startActivityForResult(intent, 102)
    }

    private fun callWeatherApi(province: String) {

        showBaseLoader(false)
        try {
            if (isNetworkAvailable(requireContext())) {
                try {
                    val request = HttpRequestVO(Constant.URL_WEATHER_MENU + province)
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpPost.METHOD_NAME

                    val callback = Handler.Callback {
                        if (it.obj != null) {
                            val responseString = it.obj.toString()
                            val responseObject = Gson().fromJson<WeatherResponse>(
                                responseString,
                                WeatherResponse::class.java
                            )

                            if (responseObject.error.message == null) {

                                rvWeather.visibility = View.VISIBLE
                                layout_nodata_weather.visibility = View.GONE
                                adapter.submitList(responseObject.cuaca)
                            } else {
                                rvWeather.visibility = View.GONE
                                layout_nodata_weather.visibility = View.VISIBLE

                            }

                        }
                        hideBaseLoader()
                        true
                    }
                    HttpRequestHandler(requireContext(), Handler(callback)).run(request)
                } catch (e: Exception) {
                    hideBaseLoader()
                }
            } else hideBaseLoader()
        } catch (e: Exception) {
            hideBaseLoader()
        }

    }

}