package com.sesolutions.ui.price

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
import com.sesolutions.ui.price.adapter.PriceItemModel
import com.sesolutions.ui.price.adapter.priceHolderAdapter
import com.sesolutions.utils.Constant
import com.sesolutions.utils.SPref
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.apache.http.client.methods.HttpPost


class PriceFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_price, container, false)
    }

    private lateinit var adapter: DelegatesAdapter<PriceItemModel>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivBack.setOnClickListener {
            onBackPressed()
        }
        toolbar.setBackgroundColor(Color.parseColor("#084B96"))
        tvTitle.text = "Harga"
        callPriceApi()
        adapter = DelegatesAdapter(
            priceHolderAdapter()
        )
        val rvWeather = view.findViewById<RecyclerView>(R.id.rvPrice)
        rvWeather.layoutManager = LinearLayoutManager(requireContext())
        rvWeather.adapter = adapter
    }

    private fun callPriceApi() {
        showBaseLoader(false)
        try {
            if (isNetworkAvailable(requireContext())) {
                try {
                    val request = HttpRequestVO("http://integrate.matani.id/home-harga.php")
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpPost.METHOD_NAME

                    val callback = Handler.Callback {
                        if (it.obj != null) {
                            val responseString = it.obj.toString()
                            val responseObject = Gson().fromJson<PriceResponse>(
                                responseString,
                                PriceResponse::class.java
                            )
                            adapter.submitList(remapItem(responseObject.harga))
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

    private fun remapItem(harga: MutableList<PriceDataResponse>): MutableList<PriceItemModel> {
        val items: MutableList<PriceItemModel> = mutableListOf()

        harga.groupBy { it.city_name }.forEach {
            items.add(PriceItemModel(it.key, it.value.map { it.price_items }.toMutableList()))
        }
        return items
    }

}