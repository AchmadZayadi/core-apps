package com.sesolutions.ui.weather

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
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.weather.weather.weatherAdapter
import com.sesolutions.utils.Constant
import com.sesolutions.utils.SPref
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.apache.http.client.methods.HttpPost

class WeatherFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    private lateinit var adapter: DelegatesAdapter<WeatherDataResponse>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DelegatesAdapter(
            weatherAdapter()
        )

        ivBack.setOnClickListener {
            onBackPressed()
        }
        toolbar.setBackgroundColor(Color.parseColor("#084B96"))
        tvTitle.text = "Cuaca"
        val rvWeather = view.findViewById<RecyclerView>(R.id.rvWeather)
        rvWeather.layoutManager = LinearLayoutManager(requireContext())
        rvWeather.adapter = adapter
        callWeatherApi()
    }

    private fun callWeatherApi() {
        showBaseLoader(false)
        try {
            if (isNetworkAvailable(requireContext())) {
                try {
                    val request = HttpRequestVO("http://integrate.matani.id/home-cuaca.php")
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpPost.METHOD_NAME

                    val callback = Handler.Callback {
                        if (it.obj != null) {
                            val responseString = it.obj.toString()
                            val responseObject = Gson().fromJson<WeatherResponse>(
                                responseString,
                                WeatherResponse::class.java
                            )
                            adapter.submitList(responseObject.cuaca)
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