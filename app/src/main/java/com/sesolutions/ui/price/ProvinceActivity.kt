package com.sesolutions.ui.price

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dizcoding.adapterdelegate.DelegatesAdapter
import com.google.android.youtube.player.internal.p
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.ui.common.BaseActivity
import com.sesolutions.ui.price.adapter.ProvinceHolderAdapter
import com.sesolutions.utils.Constant
import org.apache.http.client.methods.HttpGet


class ProvinceActivity : BaseActivity() {

    private lateinit var adapter: DelegatesAdapter<String>
    var value : String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_province)

        val intent = getIntent();
        value = intent.getStringExtra("name").toString()



        adapter = DelegatesAdapter(
            ProvinceHolderAdapter{
                val intent = Intent()
                intent.putExtra("kecamatan",it)
                setResult(100,intent)
                finish()
            }
        )

        val rvWeather = findViewById<RecyclerView>(R.id.rvProvince)
        val btnClose = findViewById<ImageView>(R.id.ic_close)

        btnClose.setOnClickListener {
            finish()
        }

        rvWeather.layoutManager = LinearLayoutManager(this)
        rvWeather.adapter = adapter

        callProvinceApi()
    }

    override fun onBackPressed() {
        //super.onBackPressed();//
        val intent_return = intent
        intent_return.putExtra("nameBack", value)
        setResult(100, intent_return)
        finish()
    }



    private fun callProvinceApi() {
        showBaseLoader(false)
        try {
            if (isNetworkAvailable(this)) {
                try {

                    val request = HttpRequestVO(Constant.URL_GET_PROVINCE)
                    request.requestMethod = HttpGet.METHOD_NAME

                    val callback = Handler.Callback {
                        if (it.obj != null) {
                            val responseString = it.obj.toString()
                            val data =
                                responseString.replace("[", "").replace("]", "").replace("\"", "")
                                    .split(",")

                            adapter.submitList(data)
                        }
                        hideBaseLoader()
                        true
                    }
                    HttpRequestHandler(this, Handler(callback)).run(request)
                } catch (e: Exception) {

                    hideBaseLoader()
                }
            } else hideBaseLoader()
        } catch (e: Exception) {

            hideBaseLoader()
        }

    }
}