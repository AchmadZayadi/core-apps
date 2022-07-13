package com.sesolutions.ui.message;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.camerahelper.CameraActivity;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.Links;
import com.sesolutions.responses.MessageInbox;
import com.sesolutions.responses.Video;
import com.sesolutions.responses.feed.Item_user;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.customviews.MentionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.postfeed.TagSuggestionAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FlowLayout;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

//import org.apache.commons.lang3.StringEscapeUtils;


public class MessageActivity extends BaseActivity implements View.OnClickListener, TextWatcher, OnUserClickedListener<Integer, String> {


    private static final int CAMERA_PIC_REQUEST = 7079;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;
    private static final int TYPE_LINK = 3;
    //private ArrayList<String> searchArrayList;
    FlowLayout.LayoutParams params;
    private AppCompatEditText etSubject;
    private AppCompatEditText etBody;
    private AppCompatImageView ivBack;
    private RelativeLayout rlAttach;
    private AppCompatImageView ivSend;
    private FlowLayout flowLayout;
    private HttpRequestHandler requestHandler;
    //private AutoCompleteTextView autoCompleteTextView;
    private AppCompatTextView tvBadge;
    //private AutoCompleteAdapter adapter;
    //private ArrayAdapter<String> adapter;
    //private Map<String, Friends> mapFriend;
    private HashMap<Integer, String> selectedMap;
    private boolean isFileAttached;
    private ProgressDialog progressDialog;
    private boolean isCameraOptionSelected;
    private String imageFilePath;
    private int attachedFileType;
    private Video videoDetail;
    private Links linkDetail;
    private AppCompatEditText etSearch;
    private MentionPopup mentionPop;
    private List<Friends> list;
    private ProgressBar pb;
    private RecyclerView rvTag;
    private TagSuggestionAdapter adapter;
    private boolean isReceipentUnchangable;
    RelativeLayout main_viewgroup;

    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {
                FilePickerBuilder.getInstance()
                        .setMaxCount(1)
                        .setActivityTheme(R.style.FilePickerTheme)
                        .showFolderView(true)
                        .enableImagePicker(true)
                        .enableVideoPicker(false)
                        .pickPhoto(MessageActivity.this);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    String message_disc="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        initToolBar(Constant.TITLE_EMPTY);
        initViews();
        main_viewgroup=findViewById(R.id.main_viewgroup);
        // mapFriend = new HashMap<>();
        selectedMap = new HashMap<>();
        params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);

        if(getIntent().hasExtra("DISCRIPTONTAG")){
            message_disc=getIntent().getStringExtra("DISCRIPTONTAG");
            etBody.setText(""+message_disc);
        }
        getBundleValues();
        setRecycleview();
    }

    private void setRecycleview() {
        rvTag = findViewById(R.id.rvTag);
        list = new ArrayList<>();

        rvTag.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTag.setLayoutManager(layoutManager);
        adapter = new TagSuggestionAdapter(list, this, this);
        rvTag.setAdapter(adapter);
    }

    private void getBundleValues() {
        try {
            if (getIntent().hasExtra(Constant.KEY_DATA)) {
                /*if has bundle value then prevent user to select multiple recipent*/
                findViewById(R.id.rl1).setVisibility(View.GONE);
                isReceipentUnchangable = true;
                List<Item_user> userList = (List<Item_user>) getIntent().getSerializableExtra(Constant.KEY_DATA);
                if (userList != null && userList.size() > 0) {
                    for (Item_user iu : userList) {
                        Friends vo = new Friends();
                        vo.setUserId(iu.getUser_id());
                        vo.setId(iu.getUser_id());
                        vo.setLabel(iu.getTitle());
                        vo.setUserImage(iu.getUser_image());
                        createChip(vo);
                    }
                    /*String image = getIntent().getStringExtra(Constant.KEY_IMAGE);
                    int id = getIntent().getIntExtra(Constant.KEY_ID, -1);
                    String title = getIntent().getStringExtra(Constant.KEY_TITLE);*/
                }

                subject = getIntent().getStringExtra(Constant.KEY_SUBJECT);
            }
        } catch (Exception e) {
            //Ignoring error
        }
    }

    private String subject;

    public void createPopUp(View v) {
        try {
            mentionPop = new MentionPopup(v.getContext(), this);
            int vertPos = RelativePopupWindow.VerticalPosition.BELOW;
            int horizPos = RelativePopupWindow.HorizontalPosition.LEFT;
            mentionPop.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void initViews() {

        new ThemeManager().applyTheme((ViewGroup) findViewById(R.id.main_viewgroup), MessageActivity.this);

        etSubject = findViewById(R.id.etSubject);
        if (!TextUtils.isEmpty(subject)) {
            etSubject.setText(subject);
        }
        etBody = findViewById(R.id.etBody);
        ivBack = findViewById(R.id.ivBack);
        rlAttach = findViewById(R.id.rlAttach);
        tvBadge = findViewById(R.id.tvBadge);
        ivSend = findViewById(R.id.icSend);
        flowLayout = findViewById(R.id.flowlayout);
        pb = findViewById(R.id.pb);

        etSearch = findViewById(R.id.etSearch);

     //   main_viewgroup.setBackgroundColor(Color.parseColor(Constant.backgroundColor));
         etSearch.setTextColor(Color.parseColor("#000000"));
        etSubject.setTextColor(Color.parseColor("#000000"));
        etBody.setTextColor(Color.parseColor("#000000"));

        ivBack.setOnClickListener(this);
        rlAttach.setOnClickListener(this);
        ivSend.setOnClickListener(this);
        etSearch.addTextChangedListener(this);

    }


  /*  @Override
    protected int getHomeIcon() {
        return R.drawable.arrow_left;
    }*/

    private void callSuggestionApi(String value) {
        if (TextUtils.isEmpty(value)) {
            pb.setVisibility(View.GONE);
            if (null != list)
                list.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(this)) {
                pb.setVisibility(View.VISIBLE);
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_SUGGEST);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_VALUE, value);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(this));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (null != response) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (null != list)
                                        list.clear();
                                    if (null != resp.getResult().getFriends() && resp.getResult().getFriends().size() > 0) {
                                        list.addAll(resp.getResult().getFriends());

                                        rvTag.setVisibility(View.VISIBLE);
                                        adapter.notifyDataSetChanged();

                                    } else {
                                        rvTag.setVisibility(View.GONE);
                                    }
                                    //adapter.notifyDataSetChanged();
                                }
                                pb.setVisibility(View.GONE);
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    requestHandler = new HttpRequestHandler(this, new Handler(callback));
                    requestHandler.execute(request);

                } catch (Exception e) {
                    pb.setVisibility(View.GONE);
                    CustomLog.e(e);
                }

            } else {
                pb.setVisibility(View.GONE);
                // Util.showSnackbar(drawerLayout, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
        }
    }

    @Override
    protected void onHomePressed() {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.icSend:
                closeKeyboard();
                sendIfValid();
                break;

            case R.id.rlAttach:
                if (isFileAttached) {
                    if (TYPE_IMAGE == attachedFileType) {
                        showAttachedImage(imageFilePath);
                    } else if (TYPE_VIDEO == attachedFileType) {
                        showAttachedImage(videoDetail.getSrc(), videoDetail.getTitle());
                    } else if (TYPE_LINK == attachedFileType) {
                        showAttachedImage(linkDetail.getImages(), linkDetail.getTitle());
                    }
                } else {
                    showAttachOptionDialog(Constant.MSG_SELECT_ATTACH_SOURCE);
                }
        }
    }

    private void showAttachedImage(String imageFilePath) {
        showAttachedImage(imageFilePath, null);
    }

    private void showAttachOptionDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(this, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_three);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), this);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    openImagePicker();
                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    showVideoSourceDialog(Constant.MSG_CHOOSE_SOURCE);
                    // takeImageFromCamera();
                }
            });

            progressDialog.findViewById(R.id.bLink).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    showLinkDialog(Constant.EMPTY);
                    //showImageChooser();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showEdittextDialog(String value) {
        final EditText taskEditText = new EditText(this);
        taskEditText.setMaxLines(1);
        taskEditText.setText(value);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.MSG_ENTER_VIDEO_URL)
                // .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton(R.string.TXT_ADD, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String videoUrl = String.valueOf(taskEditText.getText());
                        CustomLog.e("value", videoUrl);
                        callAttachVideoApi(videoUrl);
                    }
                })
                //  .setNegativeButton("Can, null)
                .create();
        dialog.show();
    }

    private void showLinkDialog(String value) {
        final EditText taskEditText = new EditText(this);
        taskEditText.setMaxLines(1);
        taskEditText.setText(value);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.MSG_ENTER_LINK)
                // .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton(R.string.TXT_ADD, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String link = String.valueOf(taskEditText.getText());
                        if (!link.startsWith("http")) {
                            link = "http://" + link;
                        }
                        CustomLog.e("value", link);
                        callPreviewLinkApi(link);
                    }
                })
                //  .setNegativeButton("Can, null)
                .create();
        dialog.show();
    }

    private void showVideoSourceDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(this, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), this);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            ((AppCompatButton) progressDialog.findViewById(R.id.bCamera)).setText(R.string.TXT_YOU_TUBE);
            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    showEdittextDialog(Constant.EMPTY);
                    // takeImageFromCamera();
                }
            });
            ((AppCompatButton) progressDialog.findViewById(R.id.bGallary)).setText(Constant.TXT_VIMEO);
            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    showEdittextDialog(Constant.EMPTY);
                    //showImageChooser();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showAttachedImage(final String filePath, final String fileName) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(this, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_image_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), this);
            AppCompatImageView ivImage = progressDialog.findViewById(R.id.ivImage);
            if (TextUtils.isEmpty(fileName)) {
               /* BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 6;
                Bitmap bitmapFile = BitmapFactory.decodeFile(filePath, options);
                ivImage.setImageBitmap(bitmapFile);*/
                ivImage.setImageDrawable(Drawable.createFromPath(filePath));
            } else {

                Util.showImageWithGlide(ivImage, filePath, this, R.drawable.placeholder_3_2);
                ((TextView) progressDialog.findViewById(R.id.tvText)).setText(fileName);
            }

            progressDialog.findViewById(R.id.tvText).setVisibility(TextUtils.isEmpty(fileName) ? View.GONE : View.VISIBLE);


            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    imageFilePath = Constant.EMPTY;
                    updateMenuItem(imageFilePath);
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void openImagePicker() {
        askForPermission(Manifest.permission.CAMERA);
    }

    private void askForPermission(String permission) {
        try {
            new TedPermission(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    /**
     * camera activity call back
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            switch (requestCode) {
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == -1 && data != null) {
                        List<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS));
                        //  setImage(photoPaths.get(0));
                        imageFilePath = photoPaths.get(0);
                        attachedFileType = TYPE_IMAGE;
                        updateMenuItem(imageFilePath);

                    }
                    break;
                case CAMERA_PIC_REQUEST:
                    if (resultCode == -1) {
                        //setImage(Constant.path);
                        CustomLog.d("CAMERA_PIC_REQUEST", Constant.path);
                        imageFilePath = Constant.path;
                        attachedFileType = TYPE_IMAGE;
                        updateMenuItem(imageFilePath);
                    }
                    break;
            }

             /*  if (requestCode == CAMERA_PIC_REQUEST && resultCode == -1) {
                CustomLog.e("on", "requestCode : " + requestCode + " resultCode : " + resultCode);
             if (requestCode == Constant.SELECT_PICTURE) {
                    CustomLog.e("inner", "requestCode : " + requestCode + " resultCode : " + resultCode);
                    // pic image from gallery
                    Uri selectedImageUri = intentdata.getData();
                    Util.FCopy(image_path_source_temp + imageName, getPath(selectedImageUri));
                }
                // CheckOrient();
                // takeImage.setImageBitmap(Image_BMP);
                //   takeImage.setScaleType(ImageView.ScaleType.FIT_XY);/*CENTER_CROP
        }*/

        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private void updateMenuItem(String file) {
        CustomLog.d("attached_file", file);
        if (TextUtils.isEmpty(file)) {
            tvBadge.setVisibility(View.GONE);
            isFileAttached = false;
        } else {

            tvBadge.setVisibility(View.VISIBLE);
            isFileAttached = true;

        }
    }

    private void sendIfValid() {
        // String body = StringEscapeUtils.escapeJava(etBody.getText().toString());
        String body = etBody.getText().toString();
        String subject = etSubject.getText().toString();
        if (TextUtils.isEmpty(body)) {
            Util.showSnackbar(etBody, Constant.MSG_BODY_REQUIRED);
            return;
        }
        if (selectedMap.size() < 1) {
            Util.showSnackbar(etSearch, Constant.MSG_RECIPIENT_REQUIRED);
            return;
        }

        String ids = "";
        for (Map.Entry<Integer, String> entry : selectedMap.entrySet()) {
            int key = entry.getKey();
            String value = entry.getValue();
            ids = ids + "," + key;
            // do stuff
        }


        callSendApi(body, subject, ids.substring(1));
    }


    public void showBaseLoader(boolean isCancelable) {
        try {
            progressDialog = ProgressDialog.show(this, "", "", true);
            progressDialog.setCancelable(isCancelable);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_progress);
            // new showBaseLoaderAsync(context).execute();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void hideBaseLoader() {
        try {
            if (!isFinishing() && progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void goToMessageChatFragment(MessageInbox vo) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constant.KEY_DATA, vo);
        startActivity(intent);
    }

    private void callSendApi(String body, String subject, final String ids) {

        try {
            if (isNetworkAvailable(this)) {

                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_COMPOSE);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                  /*  if (SPref.getInstance().getDefaultInfo(this, Constant.KEY_APPDEFAULT_DATA).getResult().isIs_core_activity()) {

                    }else {
                        request.params.put(Constant.KEY_TO_VALUES, ids);
                    }*/

                    request.params.put(Constant.KEY_TO_VALUES2, ids);

                   //
                    String fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(body);
                    request.params.put(Constant.KEY_BODY, fromServerUnicodeDecoded);
                    request.params.put(Constant.KEY_TITLE, subject);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(this));
                    request.requestMethod = HttpPost.METHOD_NAME;


                    if (isFileAttached) {
                        switch (attachedFileType) {
                            case TYPE_IMAGE:
                                request.params.put(Constant.FILE_TYPE + Constant.KEY_IMAGE, imageFilePath);
                                request.params.put(Constant.KEY_ATTACHMENT_TYPE, Constant.KEY_IMAGE);
                                break;
                            case TYPE_VIDEO:
                                request.params.put(Constant.KEY_ATTACHMENT_VARAIBLE, Constant.VALUE_ATTACHMENT_VARAIBLE_VIDEO);
                                request.params.put(Constant.KEY_ATTACHMENT_ID, videoDetail.getVideoId());
                                request.params.put(Constant.KEY_ATTACHMENT_TYPE, Constant.KEY_VIDEO);
                                break;
                            case TYPE_LINK:
                                request.params.put(Constant.KEY_ATTACHMENT_VARAIBLE, Constant.VALUE_ATTACHMENT_VARAIBLE_LINK);
                                request.params.put(Constant.KEY_ATTACHMENT_ID, linkDetail.getUri());
                                request.params.put(Constant.KEY_ATTACHMENT_TYPE, Constant.KEY_LINK);
                                break;
                        }
                    }


                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse3333333", "" + response);
                                if (null != response) {
                                    if (ids.contains(",")) {
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        MessageInbox vo = resp.getResult().getMessage();
                                        if (null != vo) {
                                            finish();
                                            goToMessageChatFragment(vo);
                                        } else {
                                            Util.showSnackbar(etBody, Constant.MSG_NOT_SENT);
                                        }
                                    } else {
                                        try {
                                            JSONObject json = new JSONObject(response);
                                            MessageInbox vo = new Gson().fromJson(json.getJSONObject("result").getJSONObject("message").toString(), MessageInbox.class);
                                            if (null != vo) {
                                                finish();
                                                goToMessageChatFragment(vo);
                                            } else {
                                                Util.showSnackbar(etBody, Constant.MSG_NOT_SENT);
                                            }
                                        } catch (JSONException e) {
                                            CustomLog.e(e);
                                        } catch (JsonSyntaxException e) {
                                            CustomLog.e(e);
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    // requestHandler = new HttpRequestHandler(this, new Handler(callback));
                    //requestHandler.execute(request);
                    new HttpImageRequestHandler(this, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                    CustomLog.e(e);
                }
            } else {
                // Util.showSnackbar(drawerLayout, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }
    }

    private void callAttachVideoApi(final String videoUrl) {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(this)) {

                try {
                    showBaseLoader(false);
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CREATE_VIDEO);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_URI, videoUrl);
                    request.params.put(Constant.KEY_C_TYPE, Constant.VALUE_C_TYPE);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getUserMasterDetail(this).getAuthToken());

                    request.requestMethod = HttpPost.METHOD_NAME;


                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (null != response) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (TextUtils.isEmpty(resp.getError())) {
                                        attachedFileType = TYPE_VIDEO;
                                        videoDetail = resp.getResult().getVideo();
                                        updateMenuItem(videoDetail.getVideoId());
                                    } else {
                                        Util.showSnackbar(etBody, resp.getErrorMessage());
                                        showEdittextDialog(videoUrl);
                                    }

                                }

                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    // requestHandler = new HttpRequestHandler(this, new Handler(callback));
                    //requestHandler.execute(request);
                    new HttpRequestHandler(this, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();

                }

            } else {
                // Util.showSnackbar(drawerLayout, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }
    }

    private void callPreviewLinkApi(final String link) {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(this)) {

                try {
                    showBaseLoader(false);
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_LINK_PREVIEW);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    String link2 = link;
                    if (!link.startsWith("http://")) {
                        link2 = "http://" + link;
                    }
                    request.params.put(Constant.KEY_URI, link2);
                    request.params.put(Constant.KEY_C_TYPE, Constant.VALUE_C_TYPE);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getUserMasterDetail(this).getAuthToken());
                    request.requestMethod = HttpPost.METHOD_NAME;


                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (null != response) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (TextUtils.isEmpty(resp.getError())) {
                                        attachedFileType = TYPE_LINK;
                                        linkDetail = resp.getResult().getLink();
                                        linkDetail.setUri(link);
                                        updateMenuItem(linkDetail.getUri());
                                    } else {
                                        Util.showSnackbar(etBody, resp.getErrorMessage());
                                        showLinkDialog(link);
                                    }
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    // requestHandler = new HttpRequestHandler(this, new Handler(callback));
                    //requestHandler.execute(request);
                    new HttpRequestHandler(this, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();

                }

            } else {
                // Util.showSnackbar(drawerLayout, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (null != requestHandler /*&& !requestHandler.isCancelled()*/) {
            requestHandler.cancel(true);
        }
        callSuggestionApi("" + s);
    }

    @Override
    public void afterTextChanged(Editable s) {
        etSearch.requestFocus();
    }


    private void createChip(Friends vo) {
        try {
            if (!selectedMap.containsKey(vo.getId())) {
                selectedMap.put(vo.getId(), vo.getLabel());
           /* selectedMap.put(vo.getId(), vo.getLabel());
            if (size < selectedMap.size()) {*/
                final TextView t = new TextView(this);
                t.setLayoutParams(params);
                t.setPadding(16, 16, 16, 16);
                t.setText(vo.getLabel() + "  X ");
                if (isReceipentUnchangable) {
                    //t.setText("name");
                    t.setText(vo.getLabel());

                }
                t.setTextColor(Color.WHITE);
                t.setTag(vo.getId());
                // t.setId(vo.getId());
                // t.setId();
                t.setBackgroundColor(Color.parseColor("#000000"));
                if (!isReceipentUnchangable) {
                    t.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            t.setVisibility(View.GONE);
                            selectedMap.remove(t.getTag());
                        }
                    });
                }
                flowLayout.addView(t);
            } else {
                CustomLog.e("add_create", "already added");
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {
        try {
            Friends vo = list.get(postion);
            createChip(vo);
            etSearch.setText(Constant.EMPTY);
            rvTag.setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }
}
