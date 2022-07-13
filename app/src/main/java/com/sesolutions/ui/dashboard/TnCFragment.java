package com.sesolutions.ui.dashboard;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.DetailsTransition;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Privacy;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.regex.Pattern;


public class TnCFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private CommonResponse.Result result;
    private Privacy vo;
    private String url;

    // adapted from post by Phil Haack and modified to match better
    private final static String tagStart =
            "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)\\>";
    private final static String tagEnd =
            "\\</\\w+\\>";
    private final static String tagSelfClosing =
            "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)/\\>";
    private final static String htmlEntity =
            "&[a-zA-Z][a-zA-Z0-9]+;";
    private final Pattern htmlPattern = Pattern.compile(
            "(" + tagStart + ".*" + tagEnd + ")|(" + tagSelfClosing + ")|(" + htmlEntity + ")",
            Pattern.DOTALL
    );

    public static TnCFragment newInstance(String url) {
        TnCFragment frag = new TnCFragment();
        frag.url = url;
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            setEnterTransition(new Slide(Gravity.BOTTOM));
            setExitTransition(new Slide(Gravity.BOTTOM));
            setSharedElementEnterTransition(new DetailsTransition());
            setAllowEnterTransitionOverlap(false);
            setAllowReturnTransitionOverlap(false);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_lyrics, container, false);
        try {
            applyTheme(v);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(" ");
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            callMusicAlbumApi();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        try {

            ((TextView) v.findViewById(R.id.tvTitle)).setText(vo.getTitle());
            v.findViewById(R.id.rlMain).setBackgroundColor(Color.parseColor(Constant.foregroundColor));
            TextView tvLyrics = v.findViewById(R.id.tvLyrics);
            if (isHtml(vo.getDescription())) {
			   tvLyrics.setText(SpanUtil.getHtmlString(vo.getDescription()));
			   tvLyrics.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
               tvLyrics.setText(vo.getDescription());
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    /**
     * Will return true if s contains HTML markup tags or entities.
     *
     * @param s String to test
     * @return true if string contains HTML
     */
    private boolean isHtml(String s) {
        boolean ret = false;
        if (s != null) {
            ret = htmlPattern.matcher(s).find();
        }
        return ret;
    }

    private void callMusicAlbumApi() {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {


                try {

                    showBaseLoader(true);
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    result = resp.getResult();
                                    if (null != result.getPrivacy()) {
                                        vo = result.getPrivacy();
                                    } else if (null != result.getTerms()) {
                                        vo = result.getTerms();
                                    }
                                    init();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {

                    hideBaseLoader();

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
