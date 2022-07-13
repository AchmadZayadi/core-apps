package com.sesolutions.ui.store.wishlist

import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.ui.common.FormHelper
import com.sesolutions.ui.welcome.Dummy
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import com.sesolutions.utils.Util
import me.riddhimanadib.formmaster.model.BaseFormElement
import me.riddhimanadib.formmaster.model.FormElementPickerSingle
import org.apache.http.client.methods.HttpPost

class AddToWishlistFragment : FormHelper(), View.OnClickListener {

    private var tvTitle: AppCompatTextView? = null
    private var result: Dummy.Result? = null
    private var notHideItemFromBottom = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_signup, container, false)
        try {
            applyTheme(v)
            init()
            callSignUpApi()
        } catch (e: Exception) {
            CustomLog.e(e)
        }

        return v
    }

    override fun initScreenData() {
        init()
        this.callSignUpApi()
    }


    private fun init() {
        v.findViewById<ImageView>(R.id.ivBack).setOnClickListener(this)
        tvTitle = v.findViewById(R.id.tvTitle)
        setTitle()
        mRecyclerView = v.findViewById(R.id.recyclerView) as androidx.recyclerview.widget.RecyclerView
    }

    private fun setTitle() {
        var id = R.string.EMPTY
        when (FORM_TYPE) {
            Constant.FormType.TYPE_ADD_WISHLIST -> id = R.string.TITLE_ADD_WISHLIST
            Constant.FormType.TYPE_ADD_ALBUM -> id = R.string.TITLE_ADD_ALBUM
            Constant.FormType.ADD_VIDEO -> id = R.string.TITLE_ADD_VIDEO
            Constant.FormType.ADD_EVENT_LIST -> id = R.string.add_event_list
            Constant.FormType.CREATE_ALBUM_OTHERS -> {
                notHideItemFromBottom = 2
                id = R.string.TAB_TITLE_ALBUM_5
            }
        }
        tvTitle!!.setText(id)
    }

    override fun onValueChanged(baseFormElement: BaseFormElement) {
        super.onValueChanged(baseFormElement)
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.type) {
                CustomLog.e("onValueChanged", "111111")
                val key = Util.getKeyFromValue(commonMap.get((baseFormElement as FormElementPickerSingle).name)!!, baseFormElement.getValue()) as String
                if (null != key) {
                    val hideOrShow = key == "0" || key == ""
                    for (i in 1 until formItems.size - notHideItemFromBottom) {
                        mFormBuilder.getAdapter().getDataset().get(i).setHidden(!hideOrShow)
                    }
                    mFormBuilder.getAdapter().notifyDataSetChanged()
                    //  mFormBuilder.getAdapter().notifyItemRangeRemoved(1, result.getFormfields().size() - 2);
                } /*else {
                    createFormUi(result);
                    mFormBuilder.getAdapter().setValueAtIndex(0, baseFormElement.getValue());
                }*/
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.ivBack -> onBackPressed()
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    private fun callSignUpApi() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false)
                try {

                    val request = HttpRequestVO(url)
                    if (null != map) {
                        request.params.putAll(map)
                    }

                    request.params[Constant.KEY_GET_FORM] = 1
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
//                    request.params[Constant.KEY_AUTH_TOKEN] = "1641b1b8453a1ccc1555046244"
                    request.requestMethod = HttpPost.METHOD_NAME
                    request.headres[Constant.KEY_COOKIE] = getCookie()
                    val callback = Handler.Callback { msg ->
                        hideBaseLoader()
                        try {
                            val response = msg.obj as String
                            CustomLog.e("repsonse", "" + response)
                            if (response != null) {
                                val err = Gson().fromJson(response, ErrorResponse::class.java)
                                if (TextUtils.isEmpty(err.error)) {
                                    val vo = Gson().fromJson(response, Dummy::class.java)
                                    result = vo.result

                                    if (Constant.FormType.CREATE_ALBUM_OTHERS == FORM_TYPE) {
                                        for (fld in result!!.formfields) {
                                            if ("album_photo" == fld.name) {
                                                fld.name = "image"
                                            }
                                        }
                                    }

                                    createFormUi(result)
                                } else {
                                    Util.showSnackbar(v, err.errorMessage)
                                    goIfPermissionDenied(err.error)
                                }
                            } else {
                                notInternetMsg(v)
                            }
                        } catch (e: Exception) {
                            CustomLog.e(e)
                        }

                        // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)

                } catch (e: Exception) {
                }

            } else {
                notInternetMsg(v)
            }

        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }


    override fun onResponseSuccess(reqCode: Int, result: Any?) {
        // (List<String>) result;
        if (null != result) {
            val filePath = (result as List<String>)[0]
            mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath)
        }
    }

    override fun onConnectionTimeout(reqCode: Int, result: String) {

    }

    companion object{
        @JvmStatic
        fun newInstance(type: Int, map: Map<String, Any>, url: String): AddToWishlistFragment {
            val fragment = AddToWishlistFragment()
            fragment.FORM_TYPE = type
            fragment.url = url
            fragment.map = map
            return fragment
        }
    }

}
