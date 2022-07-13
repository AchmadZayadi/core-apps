package com.sesolutions.ui.message;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.animate.Techniques;
import com.sesolutions.animate.YoYo;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Attachments;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Links;
import com.sesolutions.responses.MessageInbox;
import com.sesolutions.responses.Video;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.video.VideoViewActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
//import org.apache.commons.lang3.StringEscapeUtils;

public class ChatActivity extends BaseActivity implements View.OnClickListener, OnUserClickedListener<String, String> {

    private static final String TAG = "ChatActivity";

    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_VIDEO = 2;
    private static final int TYPE_LINK = 3;
    private static final int CAMERA_PIC_REQUEST = 7079;
    private static final int DELETE_MESSAGE = 101;
    private static final int VIEW_MESSAGE = 102;
    private AppCompatEditText etBody;

    private ProgressDialog progressDialog;
    private RecyclerView rvMessage;
    private ImageView fabSend;
    private ChatAdapter adapter;
    private List<MessageInbox> chatList;
    private MessageInbox messageInbox;
    private TextView tvBadge;

    private Timer timer;
    private boolean flagToBottom = true;
    public static final int API_TIME_INTERVAL = 5 * 1000;

    private boolean isFileAttached;
    private String imageFilePath;
    private int attachedFileType;
    private Video videoDetail;
    private Links linkDetail;
    private LinearLayoutManager mLinearLayoutManager;
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
                        .pickPhoto(ChatActivity.this);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new);
        initToolBar(Constant.TITLE_EMPTY);
        getBundle();
        initViews();
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        callMessageViewApi(VIEW_MESSAGE);
    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        messageInbox = (MessageInbox) bundle.getSerializable(Constant.KEY_DATA);

    }

    private void initViews() {
        new ThemeManager().applyTheme((ViewGroup) findViewById(R.id.main_viewgroup), ChatActivity.this);
        rvMessage = findViewById(R.id.rvMessage);
        etBody = findViewById(R.id.etMsg);
        GradientDrawable gdr = (GradientDrawable) etBody.getBackground();
        gdr.setColor(SesColorUtils.getForegroundColor(this));
        etBody.setBackgroundDrawable(gdr);
        fabSend = findViewById(R.id.bPost);
        fabSend.setColorFilter(Color.parseColor(Constant.menuButtonActiveTitleColor));
        AppCompatTextView tvTitle = findViewById(R.id.tvTitle);
        AppCompatImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
        //  fabSend.setTextColor(text2);
        //fabSend.setEnabled(false);
        tvTitle.setText(messageInbox.getSender());
//        tvTitle.setText("Marcus Rouse");
        tvTitle.setTextColor(Color.parseColor(Constant.text_color_1));
        ((ImageView) findViewById(R.id.icAttach)).setColorFilter(SesColorUtils.getText1Color(this));

        RelativeLayout rlAttach = findViewById(R.id.rlAttach);
        tvBadge = findViewById(R.id.tvBadge);

        rlAttach.setOnClickListener(this);
        findViewById(R.id.icSend).setOnClickListener(this);
        fabSend.setOnClickListener(this);


    }

    private void initRecyclerView() {
        chatList = new ArrayList<>();
        rvMessage.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvMessage.setLayoutManager(mLinearLayoutManager);
        adapter = new ChatAdapter(this, chatList, this);
        rvMessage.setAdapter(adapter);
    }

   /* @Override
    public void onBackPressed() {
        finish();
    }*/


/*    @Override
    protected int getHomeIcon() {
        return R.drawable.arrow_left;
    }*/

    private void callMessageViewApi(final int REQ) {
        Handler handler = new Handler();
        if (REQ == DELETE_MESSAGE) {
            try {
                if (isNetworkAvailable(ChatActivity.this)) {
                    try {
                        // showBaseLoader(true);
                        String url = REQ == DELETE_MESSAGE ? Constant.URL_DELETE_MESSAGE : Constant.URL_MESSAGE_VIEW;
                        HttpRequestVO request = new HttpRequestVO(url);

                        request.headres.put(Constant.KEY_COOKIE, getCookie());
                        request.params.put(REQ == VIEW_MESSAGE ? Constant.KEY_ID : Constant.KEY_CONVERSATION_ID, messageInbox.getConversationId());
                        request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getUserMasterDetail(ChatActivity.this).getAuthToken());
                        request.requestMethod = HttpPost.METHOD_NAME;

                        Handler.Callback callback = new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message msg) {
                                hideBaseLoader();
                                try {
                                    String response = (String) msg.obj;
                                    CustomLog.e("repsonse", "" + response);
                                    if (null != response) {
                                        Constant.MESSAGE_DELETED = true;
                                        onBackPressed();
                                    }

                                } catch (Exception e) {
                                    CustomLog.e(e);
                                }
                                // dialog.dismiss();
                                return true;
                            }
                        };
                        HttpRequestHandler requestHandler = new HttpRequestHandler(ChatActivity.this, new Handler(callback));
                        requestHandler.run(request);

                    } catch (Exception e) {
                        hideBaseLoader();
                        Log.e(TAG, "callMessageViewApi: " + e.getMessage());
                    }

                } else {
                    hideBaseLoader();
                    // Util.showSnackbar(drawerLayout, Constant.MSG_NO_INTERNET);
                }
            } catch (Exception e) {
                hideBaseLoader();
                Log.e(TAG, "callMessageViewApi: " + e.getMessage());
            }
        } else {
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (isNetworkAvailable(ChatActivity.this)) {
                                    try {
                                        // showBaseLoader(true);
                                        String url = REQ == DELETE_MESSAGE ? Constant.URL_DELETE_MESSAGE : Constant.URL_MESSAGE_VIEW;
                                        HttpRequestVO request = new HttpRequestVO(url);

                                        request.headres.put(Constant.KEY_COOKIE, getCookie());
                                        request.params.put(REQ == VIEW_MESSAGE ? Constant.KEY_ID : Constant.KEY_CONVERSATION_ID, messageInbox.getConversationId());
                                        request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getUserMasterDetail(ChatActivity.this).getAuthToken());
                                        request.requestMethod = HttpPost.METHOD_NAME;

                                        Handler.Callback callback = new Handler.Callback() {
                                            @Override
                                            public boolean handleMessage(Message msg) {
                                                // hideBaseLoader();
                                                try {
                                                    String response = (String) msg.obj;
                                                    CustomLog.e(TAG + ": Response: ", response);
                                                    if (null != response) {
                                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                                        if (TextUtils.isEmpty(resp.getError())) {
                                                            for (int i = chatList.size(); i < resp.getResult().getMessageList().size(); i++) {
                                                                chatList.add(resp.getResult().getMessageList().get(i));
                                                            }
                                                            adapter.notifyDataSetChanged();
                                                            if (flagToBottom) {
                                                                mLinearLayoutManager.scrollToPosition(chatList.size() - 1);
                                                                flagToBottom = false;
                                                            }
                                                        } else {
                                                            Util.showToast(ChatActivity.this, resp.getError());
                                                            if (resp.getError().equals(Constant.MSG_PERMISSION_ERROR)) {
                                                                onBackPressed();
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
                                        HttpRequestHandler requestHandler = new HttpRequestHandler(ChatActivity.this, new Handler(callback));
                                        requestHandler.run(request);

                                    } catch (Exception e) {
                                        // hideBaseLoader();
                                        Log.e(TAG, "run: " + e.getMessage());
                                    }

                                } else {
                                    // hideBaseLoader();
                                    // Util.showSnackbar(drawerLayout, Constant.MSG_NO_INTERNET);
                                }
                            } catch (Exception e) {
                                // hideBaseLoader();
                                Log.d(TAG, "run() returned: " + e.getMessage());
                            }
                        }
                    });
                }
            };
            timer.schedule(timerTask, 0, API_TIME_INTERVAL);
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
            case R.id.bPost:
                closeKeyboard();
                sendIfValid();
                break;
            case R.id.icSend:
                closeKeyboard();
                showDeleteDialog();
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

    public void showBaseLoader(boolean isCancelable) {
        try {
            progressDialog = ProgressDialog.show(this, "", "", true);
            progressDialog.setCancelable(isCancelable);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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


    @Override
    public boolean onItemClicked(String object1, String object2, int postion) {
        MessageInbox vo = chatList.get(postion);
        Attachments attachments = vo.getAttachments();
        try {
            if(object1!=null){
                if(object1.equalsIgnoreCase("PROFILEVIEW")){
          /*  profile_TAG=1010;
            Intent intent = new Intent(this, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE);
            intent.putExtra(Constant.KEY_ID, vo.getUserId());
            intent.putExtra(Constant.KEY_COMMENT_ID, false);
            startActivity(intent);*/
                }else {
                    if (attachments != null) {
                        CustomLog.e("attachments", "" + new Gson().toJson(attachments));
                        String type = attachments.getAttachmentType();
                        Intent intent;
                        switch (type) {
                            case Constant.TYPE_IMAGE:
                                CustomLog.e("attachments", "TYPE_IMAGE");
                                intent = new Intent(this, CommonActivity.class);
                                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.GALLARY);
                                intent.putExtra(Constant.KEY_URI, attachments.getAttachmentPhoto());
                                intent.putExtra(Constant.KEY_ID, attachments.getAttachmentId());
                                intent.putExtra(Constant.KEY_TYPE, attachments.getAttachmentType());
                                intent.putExtra(Constant.KEY_RESOURCES_TYPE, attachments.getAttachmentType());
                                startActivity(intent);
                                break;
                            case Constant.TYPE_LINK:
                                CustomLog.e("attachments", "TYPE_LINK");
                                intent = new Intent(this, CommonActivity.class);
                                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_WEBVIEW);
                                intent.putExtra(Constant.KEY_URI, attachments.getAttachmentUri());
                                intent.putExtra(Constant.KEY_TITLE, attachments.getAttachmentTitle());
                                startActivity(intent);
                                break;
                            case Constant.TYPE_VIDEO:
                                CustomLog.e("attachments", "TYPE_VIDEO");
                                intent = new Intent(this, VideoViewActivity.class);
                                // intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.VIDEO);
                                intent.putExtra(Constant.KEY_ID, attachments.getAttachmentId());
                                // intent.putExtra(Constant.KEY_TITLE, attachments.getAttachmentTitle());
                                startActivity(intent);
                                break;


                        }

                    }
                }
            }else {
                try {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) this
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData
                            .newPlainText("message", "" +  vo.getBody());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, getString(R.string.copy_message),Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            try {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) this
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("message", "" +  vo.getBody());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, getString(R.string.copy_message),Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
        return false;
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

    public void openImagePicker() {
        askForPermission(Manifest.permission.CAMERA);
    }

    public void showDeleteDialog() {
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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_MESSAGE);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callMessageViewApi(DELETE_MESSAGE);
                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
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
                .setTitle(Constant.MSG_ENTER_VIDEO_URL)
                // .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton(Constant.TXT_ADD, new DialogInterface.OnClickListener() {
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
                .setTitle(Constant.MSG_ENTER_LINK)
                // .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton(Constant.TXT_ADD, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String link = String.valueOf(taskEditText.getText());
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
            ((AppCompatButton) progressDialog.findViewById(R.id.bCamera)).setText(Constant.TXT_YOU_TUBE);
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
                /*BitmapFactory.Options options = new BitmapFactory.Options();
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
    @SuppressLint("MissingSuperCall")
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
        // fabSend.setEnabled(etBody.getText().length() != 0 || isFileAttached);
    }

    private void sendIfValid() {
        try {
            String body = etBody.getText().toString();
            //String body = StringEscapeUtils.escapeJava(etBody.getText().toString());
            String subject = messageInbox.getTitle();
           // if (TextUtils.isEmpty(body) && !isFileAttached) {
            if (TextUtils.isEmpty(body)) {
                // Util.showSnackbar(etBody, Constant.MSG_BODY_REQUIRED);
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);
                startAnimation(fabSend, Techniques.SHAKE, 400);
                return;
            }


            String ids = "" + messageInbox.getConversationId();
            callSendApi(body, subject, ids);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void startAnimation(final View cv, final int animateType, int duration) {

        try {
            Techniques technique = Techniques.values()[animateType];
            YoYo.with(technique)
                    .duration(duration)
                    .repeat(1)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .playOn(cv);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callSendApi(String body, String subject, final String ids) {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(this)) {

                try {
                    showBaseLoader(false);
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_MESSAGE_VIEW);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    //  request.params.put(Constant.KEY_TO_VALUES, ids);
                    request.params.put(Constant.KEY_ID, ids);
                    request.params.put(Constant.KEY_BODY, body);
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
                                CustomLog.e("repsonse", "" + response);
                                if (null != response) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    List<MessageInbox> list = resp.getResult().getMessageList();
                                    if (null != list && list.size() > 0) {
                                        imageFilePath = Constant.EMPTY;
                                        linkDetail = null;
                                        videoDetail = null;
                                        updateMenuItem(imageFilePath);
                                        // chatList.clear();
                                        // chatList.addAll(list);
                                        /*if (chatList.size() != list.size()) {
                                            chatList.add(list.get(list.size() - 1));
                                        }*/
                                        if (chatList.size() > 0) {
                                            adapter.notifyDataSetChanged();
                                            mLinearLayoutManager.scrollToPosition(chatList.size() - 1);
                                            etBody.setText(Constant.EMPTY);
                                        }
                                    } else {
                                        Util.showSnackbar(etBody, Constant.MSG_NOT_SENT);
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
                    Log.e(TAG, "callSendApi: " + e.getMessage());
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
                    String link2 = link;
                    if (!link.startsWith("http://")) {
                        link2 = "http://" + link;
                    }
                    request.params.put(Constant.KEY_URI, link2);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_URI, link);
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
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }
}
