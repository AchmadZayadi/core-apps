package com.sesolutions.ui.price

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
import com.sesolutions.ui.price.adapter.PriceItemModel
import com.sesolutions.ui.price.adapter.priceHolderAdapter
import com.sesolutions.utils.Constant
import com.sesolutions.utils.SPref
import kotlinx.android.synthetic.main.fragment_price.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.apache.http.client.methods.HttpPost
import java.text.SimpleDateFormat
import java.util.*


class PriceFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_price, container, false)
    }

    private lateinit var adapter: DelegatesAdapter<PriceItemModel>
    var kecamatan: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()


        kecamatan = SPref.getInstance().getKecamatan(context)


        if (kecamatan.contains("/")){
            val splittedKecamatan = kecamatan.split("/")
            if (splittedKecamatan.isNotEmpty()){
                kecamatan = splittedKecamatan[0].trim()
                priceState.text = kecamatan
            }
        }else{
            priceState.text = kecamatan
        }

        callPriceApi(kecamatan.replace(" ", "%20"))

        adapter = DelegatesAdapter(
            priceHolderAdapter()
        )
        val rvWeather = view.findViewById<RecyclerView>(R.id.rvPrice)
        rvWeather.layoutManager = LinearLayoutManager(requireContext())
        rvWeather.adapter = adapter

        layout_province.setOnClickListener {
            gotoProvince()
        }
    }


    fun init() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
        val today: Date = Calendar.getInstance().getTime()
        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val formattedDate: String = df.format(today)
        tvSumber.text = formattedDate + " - " + "Sumber: "
        tvLinkWeb.setOnClickListener {
            val intent = Intent(
                context,
                WebViewActivity::class.java
            )
            intent.putExtra("web", "https://panelharga.badanpangan.go.id/")
            intent.putExtra("title", "Panel Harga")
            startActivity(intent)
        }

        toolbar.setBackgroundColor(Color.parseColor("#084B96"))
        tvTitle.text = "Harga Produsen"

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        kecamatan = data?.getStringExtra("kecamatan")?:""
        if (kecamatan.isNotEmpty() || kecamatan.isNotBlank()){
            priceState.text = kecamatan
            callPriceApi(kecamatan.replace(" ", "%20"))
        }


    }


    private fun callPriceApi(kecamatan: String) {

        showBaseLoader(false)
        try {
            if (isNetworkAvailable(requireContext())) {
                try {


                    var url: String = Constant.URL_PRICE_MENU + kecamatan


                    val request = HttpRequestVO(url)
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpPost.METHOD_NAME


                    val callback = Handler.Callback {
                        if (it.obj != null) {


                            val responseString = it.obj.toString()
                            val responseObject = Gson().fromJson<PriceResponse>(
                                responseString,
                                PriceResponse::class.java
                            )
                            if (responseObject.error.message == null) {

                                layout_nodata.visibility = View.GONE
                                rvPrice.visibility = View.VISIBLE
                                adapter.submitList(remapItem(responseObject.harga))

                            } else {

                                layout_nodata.visibility = View.VISIBLE
                                rvPrice.visibility = View.GONE
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


    fun gotoProvince() {
        val intent = Intent(context, ProvinceActivity::class.java)
        intent.putExtra("name", kecamatan)
        startActivityForResult(intent, 100)
    }

    private fun remapItem(harga: MutableList<PriceDataResponse>): MutableList<PriceItemModel> {
        val items: MutableList<PriceItemModel> = mutableListOf()

        harga.groupBy { it.city_name }.forEach {
            items.add(PriceItemModel(it.key, it.value.map { it.price_items }.toMutableList()))
        }
        return items
    }

}