package com.sesolutions.imageeditengine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.droidninja.imageeditengine.AnimationHelper;
import com.droidninja.imageeditengine.BaseFrag;
import com.droidninja.imageeditengine.Constants;
import com.droidninja.imageeditengine.OnUserClick;
import com.droidninja.imageeditengine.adapters.FilterImageAdapter;
import com.droidninja.imageeditengine.filter.ApplyFilterTask;
import com.droidninja.imageeditengine.filter.GetFiltersTask;
import com.droidninja.imageeditengine.filter.ProcessingImage;
import com.droidninja.imageeditengine.model.ImageFilter;
import com.droidninja.imageeditengine.utils.FilterHelper;
import com.droidninja.imageeditengine.utils.FilterTouchListener;
import com.droidninja.imageeditengine.utils.Matrix3;
import com.droidninja.imageeditengine.utils.TaskCallback;
import com.droidninja.imageeditengine.utils.Utility;
import com.droidninja.imageeditengine.views.PhotoEditorView;
import com.droidninja.imageeditengine.views.VerticalSlideColorPicker;
import com.droidninja.imageeditengine.views.ViewTouchListener;
import com.droidninja.imageeditengine.views.imagezoom.ImageViewTouch;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.responses.unsplash.SesWallpaper;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.dashboard.composervo.ActivityStikersMenu;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import org.apache.http.client.methods.HttpGet;

import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class PhotoEditorFragment extends BaseFrag
        implements View.OnClickListener, ViewTouchListener,
        FilterImageAdapter.FilterImageAdapterListener, OnUserClickedListener<Integer, Object>, OnUserClick, SeekBar.OnSeekBarChangeListener {

    private static final int REQ_WALLPAPER = 101;
    private boolean CAN_SHOW_BACK_BUTTON = true;
    private View view;
    ImageViewTouch mainImageView;
    ImageView cropButton, backButton;
    ImageView stickerButton;
    ImageView addTextButton;
    PhotoEditorView photoEditorView;
    ImageView paintButton;
    ImageView deleteButton;
    ImageView undoButton;
    VerticalSlideColorPicker colorPickerView;
    //CustomPaintView paintEditView;
    View toolbarLayout;
    RecyclerView filterRecylerview, rvWallpaper, rvFont;
    View filterLayout;
    View filterLabel;
    FloatingActionButton doneBtn;
    private Bitmap mainBitmap;
    private LruCache<Integer, Bitmap> cacheStack;
    private int filterLayoutHeight;
    private OnUserClickedListener<Integer, Object> mListener;

    protected int currentMode;
    private ImageFilter selectedFilter;
    private Bitmap originalBitmap;
    private AppCompatSeekBar sbContrast;
    private int mContrast = 0;
    private ArrayList<Integer> lastEditedItemList = new ArrayList<>();

    public static PhotoEditorFragment newInstance(String imagePath) {
        Bundle bundle = new Bundle();
        bundle.putString(ImageEditor.EXTRA_IMAGE_PATH, imagePath);
        PhotoEditorFragment photoEditorFragment = new PhotoEditorFragment();
        photoEditorFragment.setArguments(bundle);
        return photoEditorFragment;
    }

    public PhotoEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            isComingFromBack = true;
            return view;
        } else {
            isComingFromBack = false;
        }
        view = inflater.inflate(R.layout.fragment_photo_editor, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnUserClickedListener<Integer, Object>) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    private ProgressDialog progressDialog;

    public void showCustomBaseLoader(boolean isCancelable) {
        try {
            progressDialog = ProgressDialog.show(getContext(), "", "", true);
            progressDialog.setCancelable(isCancelable);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.layout_custom_loader);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void hideBaseLoader() {
        try {
            if (getActivity() != null && !getActivity().isFinishing() && progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            switch (requestCode) {
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == -1 && data != null) {
                        List<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS));
                        if (photoPaths.size() > 0) {
                            changeWallpaper(photoPaths.get(0));
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        switch (eventType) {
            case REQ_WALLPAPER:
                try {
                    if (null != data) {
                        JsonElement element = new Gson().fromJson("" + data, JsonElement.class);
                        if (element.isJsonArray()) {
                            JsonArray arr = element.getAsJsonArray();
                            for (JsonElement jsonElement : arr) {
                                wallpaperList.add(new Gson().fromJson(jsonElement, SesWallpaper.class));
                            }

                            adapterBg.notifyDataSetChanged();
                        }
                    }
                } catch (JsonSyntaxException e) {
                    CustomLog.e(e);
                }
                break;
            case Constants.Events.STICKER:
                //open stickerchild fragment if position==-1
                if (position == -1) {
                    StickerChildDialogFragment.newInstance(null, (String) data, this).show(getActivity().getSupportFragmentManager(), "stickers");
                } else {
                    setStickerBitMap((String) data);
                }
                break;

            case Constants.Events.TASK:
                mListener.onItemClicked(Constants.Events.TASK, currentMode, position);
                break;
            case Constants.Events.HIDE_BOTTOM_SHEET:
                if (Constants.TASK_FONT == position) {
                    mBottomSheetOptions.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                break;
            case Constants.Events.MODE_CHANGE:
                setMode(position);
                break;
            case Constants.Events.FONT:
                if (position < 0) {
                    List<Font> tempList = new ArrayList<>(fontList);
                    tempList.remove(0);
                    new FontHelper().initScreenData(llBottomSheet, tempList, this);
                    mBottomSheetOptions.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                   /* getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.container, FontFragment.newInstance(tempList), FontFragment.class.getName())
                            .addToBackStack(null)
                            .commit();*/
                } else {
                    fetchSelectedFont("" + data);
                }
                break;
            case Constant.Events.BG_ATTACH:
                if (wallpaperList.get(position).getType() == ImageEditor.TYPE_WALLPAPER_MORE) {
                    List<SesWallpaper> tempList = new ArrayList<>(wallpaperList);
                    tempList.remove(0);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.container, WallpaperFragment.newInstance(tempList), WallpaperFragment.class.getName())
                            .addToBackStack(null)
                            .commit();
                } else if (wallpaperList.get(position).getType() == ImageEditor.TYPE_WALLPAPER_GALLERY) {
                    //check permisssion and open gallery open gallery
                    new TedPermission(getContext())
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    showImageChooser();
                                }

                                @Override
                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                                }
                            })
                            .setDeniedMessage(getString(R.string.MSG_PERMISSION_DENIED))
                            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .check();

                } else {
                    mContrast = 0;
                    sbContrast.setProgress(50);
                    showCustomBaseLoader(false);
                    setbackgroungImageAndFilters(wallpaperList.get(position).getDownload());
                }
                break;

            case Constants.Events.STICKER_MORE:
                List<ActivityStikersMenu> menu = new ArrayList<>();
                menu.add(new ActivityStikersMenu(getString(R.string.feeling)));
                menu.add(new ActivityStikersMenu(getString(R.string.stickers)));
                // menu.add(new ActivityStikersMenu(getString(R.string.activities)));
                ((BaseActivity) getActivity()).activity = new Activity();
                StickerDialogFragment.newInstance(false, this).show(getActivity().getSupportFragmentManager(), "sticker");

               /* getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, FeelingStickerFragment.newInstance(menu), FeelingStickerFragment.class.getName())
                        .addToBackStack(null)
                        .commit();*/
                break;
            case Constants.Events.VIEW_EDITED:
                lastEditedItemList.add(position);
                updateUndoButtonVisibility();
                break;
        }
        return false;
    }

    private void showImageChooser() {
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setActivityTheme(R.style.FilePickerTheme)
                .showFolderView(true)
                .enableImagePicker(true)
                .enableVideoPicker(false)
                .pickPhoto(this);
    }

    public void fetchSelectedFont(String queryString) {
        //handle font change
        final FontRequest request = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                queryString,
                R.array.com_google_android_gms_fonts_certs);
        FontsContractCompat
                .requestFont(getContext(), request, callback,
                        getHandlerThreadHandler());
    }

    private void updateUndoButtonVisibility() {
        if (lastEditedItemList.size() > 0) {
            undoButton.setVisibility(View.VISIBLE);
        } else {
            undoButton.setVisibility(View.GONE);
        }
    }

    private Handler mHandler = null;

    private Handler getHandlerThreadHandler() {
        if (mHandler == null) {
            HandlerThread handlerThread = new HandlerThread("fonts");
            handlerThread.start();
            mHandler = new Handler(handlerThread.getLooper());
        }
        return mHandler;
    }

    final FontsContractCompat.FontRequestCallback callback = new FontsContractCompat.FontRequestCallback() {
        @Override
        public void onTypefaceRetrieved(Typeface typeface) {
            photoEditorView.setTypeface(typeface);
            photoEditorView.updateFont();
        }

        @Override
        public void onTypefaceRequestFailed(int reason) {
            CustomLog.e("font", "failed because of reason " + reason);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mContrast = (int) ((progress * 5.1f) - 255);


        //imageView.setImageBitmap(changeBitmapContrastBrightness(BitmapFactory.decodeResource(getResources(), R.drawable.lhota), (float) progress / 100f, 1));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //setImageBitmap(mainBitmap);
        setImageBitmap(changeBitmapContrastBrightness(mainBitmap, 1, mContrast));
    }

    public void changeWallpaper(String wallpaperUrl) {
        if (!TextUtils.isEmpty(wallpaperUrl)) {
            mContrast = 0;
            sbContrast.setProgress(50);
            showCustomBaseLoader(false);
            setbackgroungImageAndFilters(wallpaperUrl);
        }
    }


    public interface OnFragmentInteractionListener {
        void onCropClicked(Bitmap bitmap);

        void onDoneClicked(String imagePath);
    }

    public void setImageBitmap(Bitmap bitmap) {
        mainImageView.setImageBitmap(bitmap);
        mainImageView.post(new Runnable() {
            @Override
            public void run() {
                photoEditorView.setBounds(mainImageView.getBitmapRect());
            }
        });
    }

    public void setImageWithRect(Rect rect) {
        mainBitmap = getScaledBitmap(getCroppedBitmap(getBitmapCache(originalBitmap), rect));
        mainImageView.setImageBitmap(mainBitmap);
        mainImageView.post(new Runnable() {
            @Override
            public void run() {
                photoEditorView.setBounds(mainImageView.getBitmapRect());
            }
        });

        new GetFiltersTask(new TaskCallback<ArrayList<ImageFilter>>() {
            @Override
            public void onTaskDone(ArrayList<ImageFilter> data) {
                FilterImageAdapter filterImageAdapter = (FilterImageAdapter) filterRecylerview.getAdapter();
                if (filterImageAdapter != null) {
                    filterImageAdapter.setData(data);
                    filterImageAdapter.notifyDataSetChanged();
                }
            }
        }, mainBitmap).execute();
    }

    private Bitmap getScaledBitmap(Bitmap resource) {
        int currentBitmapWidth = resource.getWidth();
        int currentBitmapHeight = resource.getHeight();
        int ivWidth = mainImageView.getWidth();
        int newHeight = (int) Math.floor(
                (double) currentBitmapHeight * ((double) ivWidth / (double) currentBitmapWidth));
        return Bitmap.createScaledBitmap(resource, ivWidth, newHeight, true);
    }

    private Bitmap getCroppedBitmap(Bitmap srcBitmap, Rect rect) {
        // Crop the subset from the original Bitmap.
        return Bitmap.createBitmap(srcBitmap,
                rect.left,
                rect.top,
                (rect.right - rect.left),
                (rect.bottom - rect.top));
    }

    public void reset() {
        photoEditorView.reset();
    }

    protected void initView(View view) {
        mainImageView = view.findViewById(R.id.image_iv);
        cropButton = view.findViewById(R.id.crop_btn);
        backButton = view.findViewById(R.id.back_iv);
        stickerButton = view.findViewById(R.id.stickers_btn);
        addTextButton = view.findViewById(R.id.add_text_btn);
        deleteButton = view.findViewById(R.id.delete_view);
        undoButton = view.findViewById(R.id.undo_btn);
        photoEditorView = view.findViewById(R.id.photo_editor_view);
        photoEditorView.setListener(this);
        paintButton = view.findViewById(R.id.paint_btn);
        colorPickerView = view.findViewById(R.id.color_picker_view);
        //paintEditView = findViewById(R.id.paint_edit_view);
        toolbarLayout = view.findViewById(R.id.toolbar_layout);
        filterRecylerview = view.findViewById(R.id.filter_list_rv);
        rvWallpaper = view.findViewById(R.id.rvWallpaper);
        rvFont = view.findViewById(R.id.rvFont);
        filterLayout = view.findViewById(R.id.filter_list_layout);
        filterLabel = view.findViewById(R.id.filter_label);


        setUpBottomSheet(view);

        setFilterTabs(view);
        sbContrast = view.findViewById(R.id.sbContrast);
        sbContrast.setProgress(50);
        sbContrast.setOnSeekBarChangeListener(this);

        if (getArguments() != null && getActivity() != null && getActivity().getIntent() != null) {
            final String imagePath = getArguments().getString(ImageEditor.EXTRA_IMAGE_PATH);
            //mainImageView.post(new Runnable() {
            //  @Override public void run() {
            //  mainBitmap = Utility.decodeBitmap(imagePath,mainImageView.getWidth(),mainImageView.getHeight());
            //
            //  }
            //});
            setbackgroungImageAndFilters(imagePath);
            setWallpaperRecyclerview();
            setFontRecyclerview();
            fetchWallpapers(1);

            Intent intent = getActivity().getIntent();
            setVisibility(addTextButton, intent.getBooleanExtra(ImageEditor.EXTRA_IS_TEXT_MODE, false));
            setVisibility(cropButton, intent.getBooleanExtra(ImageEditor.EXTRA_IS_CROP_MODE, false));
            setVisibility(stickerButton, intent.getBooleanExtra(ImageEditor.EXTRA_IS_STICKER_MODE, false));
            setVisibility(paintButton, intent.getBooleanExtra(ImageEditor.EXTRA_IS_PAINT_MODE, false));
            setVisibility(filterLayout, intent.getBooleanExtra(ImageEditor.EXTRA_HAS_FILTERS, false));
            String quoteTitle = intent.getStringExtra(ImageEditor.EXTRA_QUOTE_TITLE);
            String quoteSource = intent.getStringExtra(ImageEditor.EXTRA_QUOTE_SOURCE);
            CAN_SHOW_BACK_BUTTON = intent.getBooleanExtra(ImageEditor.EXTRA_BACK_BUTTON, true);
            if (intent.getBooleanExtra(ImageEditor.EXTRA_SHOW_DONE_BUTTON, false)) {
                doneBtn = view.findViewById(R.id.done_btn);
                doneBtn.setOnClickListener(this);
            } else {
                view.findViewById(R.id.done_btn).setVisibility(View.GONE);
            }

            photoEditorView.setImageView(mainImageView, deleteButton, this);
            //stickerEditorView.setImageView(mainImageView, deleteButton,this);
            cropButton.setOnClickListener(this);
            stickerButton.setOnClickListener(this);
            addTextButton.setOnClickListener(this);
            paintButton.setOnClickListener(this);

            undoButton.setOnClickListener(this);
            backButton.setOnClickListener(this);


            colorPickerView.setOnColorChangeListener(
                    new VerticalSlideColorPicker.OnColorChangeListener() {
                        @Override
                        public void onColorChange(int selectedColor) {
                            if (currentMode == Constants.MODE_PAINT) {
                                paintButton.setBackground(
                                        Utility.tintDrawable(getContext(), R.drawable.circle, selectedColor));
                                photoEditorView.setColor(selectedColor);
                            } else if (currentMode == Constants.MODE_ADD_TEXT) {
                                addTextButton.setBackground(
                                        Utility.tintDrawable(getContext(), R.drawable.circle, selectedColor));
                                photoEditorView.setTextColor(selectedColor);
                            }
                        }
                    });
            photoEditorView.setColor(colorPickerView.getDefaultColor());
            photoEditorView.setTextColor(colorPickerView.getDefaultColor());

            if (intent.getBooleanExtra(ImageEditor.EXTRA_HAS_FILTERS, false)) {
                filterLayout.post(new Runnable() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void run() {
                        filterLayoutHeight = filterLayout.getHeight();
                        filterLayout.setTranslationY(filterLayoutHeight);
                        photoEditorView.setOnTouchListener(
                                new FilterTouchListener(filterLayout, filterLayoutHeight, mainImageView,
                                        photoEditorView, filterLabel, doneBtn));
                    }
                });

                FilterHelper filterHelper = new FilterHelper();
                filterRecylerview.setLayoutManager(
                        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                FilterImageAdapter filterImageAdapter =
                        new FilterImageAdapter(filterHelper.getFilters(), this);
                filterRecylerview.setAdapter(filterImageAdapter);
                CustomLog.e("quoteTitle", "" + quoteTitle);

                if (!TextUtils.isEmpty(quoteTitle)) {
                    if (!TextUtils.isEmpty(quoteSource)) {
                        quoteTitle += "\n\n-" + quoteSource;
                    }
                    setMode(Constants.MODE_ADD_TEXT);
                    photoEditorView.createText(quoteTitle);
                }
            }
        }
    }

    private BottomSheetBehavior<View> mBottomSheetOptions;
    private View llBottomSheet;

    private void setUpBottomSheet(View v) {
        llBottomSheet = v.findViewById(R.id.llBottomSheet);
        mBottomSheetOptions = BottomSheetBehavior.from(llBottomSheet);
        // mBottomSheetOptions.setPeekHeight(context.getResources().getDimensionPixelSize(R.dimen.height_qa_bottom_sheet));
        // mBottomSheetOptions.setBottomSheetCallback(bottomSheetListener);
        mBottomSheetOptions.setHideable(true);
        mBottomSheetOptions.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private List<SesWallpaper> wallpaperList;
    private WallpaperAdapter adapterBg;
    private FontAdapter adapterFont;

    private void setWallpaperRecyclerview() {
        wallpaperList = new ArrayList<>();
        wallpaperList.add(new SesWallpaper(ImageEditor.TYPE_WALLPAPER_MORE));
        wallpaperList.add(new SesWallpaper(ImageEditor.TYPE_WALLPAPER_GALLERY));
        rvWallpaper.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvWallpaper.setLayoutManager(layoutManager);
        adapterBg = new WallpaperAdapter(wallpaperList, getContext(), this);
        rvWallpaper.setAdapter(adapterBg);
    }

    private List<Font> fontList;

    private void setFontRecyclerview() {
        fontList = new ArrayList<>();
        fontList.add(new Font());
        rvFont.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvFont.setLayoutManager(layoutManager);
        adapterFont = new FontAdapter(fontList, getContext(), this);
        rvFont.setAdapter(adapterFont);
        DownloadableFontList.requestDownloadableFontList(fontListCallback, getString(R.string.font_api_key));
    }

    private void fetchWallpapers(int page) {
        new ApiController(Constant.URL_UNPLASH_PHOTOS + page, null, getContext(), this, REQ_WALLPAPER)
                .setExtraKey(page)
                .setPostType(HttpGet.METHOD_NAME).execute();
    }

    private int selectedFilterTab;

    List<String> tabItems;

    private void setFilterTabs(View view) {
        tabItems = new ArrayList<>();

        TabLayout tabLayout = view.findViewById(R.id.tabFilter);
        //tabLayout.setSelectedTabIndicatorColor(menuButtonActiveTitleColor);
        //tabLayout.setTabTextColors(menuButtonTitleColor, menuButtonActiveTitleColor);
        //tabLayout.setBackgroundColor(menuButtonBackgroundColor);
        tabItems.add("filter");
        tabLayout.addTab(tabLayout.newTab().setText(R.string.filters), true);

        if (getActivity().getIntent().getBooleanExtra(ImageEditor.EXTRA_IS_WALLPAPER, false)) {
            tabItems.add("wallpaper");
            tabLayout.addTab(tabLayout.newTab().setText(R.string.wallpaper));
        }
        tabItems.add("font");
        tabLayout.addTab(tabLayout.newTab().setText(R.string.fonts));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedFilterTab = tab.getPosition();
                toggleFilterTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void toggleFilterTab() {
        Log.d("toggleFilterTab", "tab changed_ " + selectedFilterTab);
        if ("wallpaper".equals(tabItems.get(selectedFilterTab))) {
            filterRecylerview.setVisibility(View.GONE);
            rvFont.setVisibility(View.GONE);
            rvWallpaper.setVisibility(View.VISIBLE);
        } else if ("filter".equals(tabItems.get(selectedFilterTab))) {
            rvWallpaper.setVisibility(View.GONE);
            rvFont.setVisibility(View.GONE);
            filterRecylerview.setVisibility(View.VISIBLE);
        } else {
            rvWallpaper.setVisibility(View.GONE);
            filterRecylerview.setVisibility(View.GONE);
            rvFont.setVisibility(View.VISIBLE);
        }

    }

    public void setStickerBitMap(String imagePath) {

        Glide.with(this).asBitmap().load(imagePath).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource,
                                        @Nullable Transition<? super Bitmap> transition) {
                hideBaseLoader();
                try {
                    photoEditorView.onItemClick(resource);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void setbackgroungImageAndFilters(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            setbackgroungImageAndFilters(drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.bg_white)));
            return;
        }

        Glide.with(this).asBitmap().load(imagePath).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                hideBaseLoader();
                setbackgroungImageAndFilters(resource);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                CustomLog.e("GLIDE", "error in loading url:" + imagePath);
                hideBaseLoader();
                super.onLoadFailed(errorDrawable);
            }
        });
    }

    private void setbackgroungImageAndFilters(Bitmap resource) {
        try {
            int currentBitmapWidth = resource.getWidth();
            int currentBitmapHeight = resource.getHeight();
            int ivWidth = mainImageView.getWidth();
            int newHeight = (int) Math.floor(
                    (double) currentBitmapHeight * ((double) ivWidth / (double) currentBitmapWidth));
            originalBitmap = Bitmap.createScaledBitmap(resource, ivWidth, newHeight, true);
            mainBitmap = originalBitmap;
            setImageBitmap(mainBitmap);

            new GetFiltersTask(data -> {
                FilterImageAdapter filterImageAdapter = (FilterImageAdapter) filterRecylerview.getAdapter();
                if (filterImageAdapter != null) {
                    filterImageAdapter.setData(data);
                    filterImageAdapter.notifyDataSetChanged();
                }
            }, mainBitmap).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param bmp        input bitmap
     * @param contrast   0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    protected void onModeChanged(int currentMode) {
        if (CAN_SHOW_BACK_BUTTON)
            backButton.setVisibility(currentMode == Constants.MODE_NONE ? View.VISIBLE : View.GONE);
        mListener.onItemClicked(Constants.Events.TASK, currentMode, currentMode == Constants.MODE_NONE ? Constants.TASK_SHOW_CAPTION : Constants.TASK_HIDE_CAPTION);
        Log.i(ImageEditActivity.class.getSimpleName(), "CM: " + currentMode);
        onStickerMode(currentMode == Constants.MODE_STICKER);
        onAddTextMode(currentMode == Constants.MODE_ADD_TEXT);
        onPaintMode(currentMode == Constants.MODE_PAINT);

        if (currentMode == Constants.MODE_PAINT || currentMode == Constants.MODE_ADD_TEXT) {
            AnimationHelper.animate(getContext(), colorPickerView, R.anim.slide_in_right, View.VISIBLE,
                    null);
        } else {
            AnimationHelper.animate(getContext(), colorPickerView, R.anim.slide_out_right, View.INVISIBLE,
                    null);
        }
    }

    DownloadableFontList.FontListCallback fontListCallback = new DownloadableFontList.FontListCallback() {
        @Override
        public void onFontListRetrieved(FontList list) {
            // Do your work here
            CustomLog.e("font", "success");
            fontList.addAll(list.getFontArrayList());
            adapterFont.getFilter().filter("");
        }

        @Override
        public void onTypefaceRequestFailed(int reason) {
            CustomLog.e("font", "failure_" + reason);
        }
    };

    @Override
    public void onClick(final View view) {
        int id = view.getId();
        switch (id) {
            case R.id.crop_btn:
                if (selectedFilter != null) {
                    new ApplyFilterTask(new TaskCallback<Bitmap>() {
                        @Override
                        public void onTaskDone(Bitmap data) {
                            if (data != null) {
                                mListener.onItemClicked(Constants.Events.TASK, getBitmapCache(data), Constants.TASK_CROP);
                                photoEditorView.hidePaintView();
                            }
                        }
                    }, Bitmap.createBitmap(originalBitmap)).execute(selectedFilter);
                } else {
                    mListener.onItemClicked(Constants.Events.TASK, getBitmapCache(originalBitmap), Constants.TASK_CROP);
                    photoEditorView.hidePaintView();
                }
                break;
            case R.id.stickers_btn:
                setMode(Constants.MODE_STICKER);
                break;
            case R.id.add_text_btn:
                setMode(Constants.MODE_ADD_TEXT);
                break;
            case R.id.paint_btn:
                setMode(Constants.MODE_PAINT);
                break;
            case R.id.back_iv:
                getActivity().onBackPressed();
                break;
            case R.id.undo_btn:
                onUndoPressed();
                break;
            case R.id.done_btn:
                processFinalImage(-1);
                break;
        }

        if (currentMode != Constants.MODE_NONE) {
            filterLabel.setAlpha(0f);
            mainImageView.animate().scaleX(1f);
            photoEditorView.animate().scaleX(1f);
            mainImageView.animate().scaleY(1f);
            photoEditorView.animate().scaleY(1f);
            filterLayout.animate().translationY(filterLayoutHeight);
            //touchView.setVisibility(View.GONE);
        } else {
            filterLabel.setAlpha(1f);
            //touchView.setVisibility(View.VISIBLE);
        }
    }

    public void processFinalImage(int position) {
        if (selectedFilter != null) {
            new ApplyFilterTask(data -> {
                if (data != null) {

                    new ProcessingImage(getBitmapCache(data), Utility.getCacheFilePath(view.getContext()),
                            data1 -> mListener.onItemClicked(Constants.Events.DONE, data1, position)).execute();
                }
            }, Bitmap.createBitmap(mainBitmap)).execute(selectedFilter);
        } else {
            new ProcessingImage(getBitmapCache(changeBitmapContrastBrightness(mainBitmap, 1, mContrast)), Utility.getCacheFilePath(view.getContext()),
                    data -> mListener.onItemClicked(Constants.Events.DONE, data, position)).execute();
        }
    }

    private void onAddTextMode(boolean status) {
        if (status) {
            addTextButton.setBackground(
                    Utility.tintDrawable(getContext(), R.drawable.circle, photoEditorView.getColor()));
            //photoEditorView.setTextColor(photoEditorView.getColor());
            photoEditorView.addText();
        } else {
            addTextButton.setBackground(null);
            photoEditorView.hideTextMode();
        }
    }

    private void onPaintMode(boolean status) {
        if (status) {
            paintButton.setBackground(
                    Utility.tintDrawable(getContext(), R.drawable.circle, photoEditorView.getColor()));
            photoEditorView.showPaintView();
            //paintEditView.setVisibility(View.VISIBLE);
        } else {
            paintButton.setBackground(null);
            photoEditorView.hidePaintView();
            //photoEditorView.enableTouch(true);
            //paintEditView.setVisibility(View.GONE);
        }
    }

    private void onStickerMode(boolean status) {
        if (status) {
            stickerButton.setBackground(
                    Utility.tintDrawable(getContext(), R.drawable.circle, photoEditorView.getColor()));
            if (getActivity() != null && getActivity().getIntent() != null) {
                String folderName = getActivity().getIntent().getStringExtra(ImageEditor.EXTRA_STICKER_FOLDER_NAME);
                photoEditorView.showStickers(folderName);
            }
        } else {
            stickerButton.setBackground(null);
            photoEditorView.hideStickers();
        }
    }

    @Override
    public void onStartViewChangeListener(final View view) {
        Log.i(ImageEditActivity.class.getSimpleName(), "onStartViewChangeListener" + "" + view.getId());
        toolbarLayout.setVisibility(View.GONE);
        AnimationHelper.animate(getContext(), deleteButton, R.anim.fade_in_medium, View.VISIBLE, null);
    }

    @Override
    public void onStopViewChangeListener(View view) {
        Log.i(ImageEditActivity.class.getSimpleName(), "onStopViewChangeListener" + "" + view.getId());
        deleteButton.setVisibility(View.GONE);
        AnimationHelper.animate(getContext(), toolbarLayout, R.anim.fade_in_medium, View.VISIBLE, null);
    }

    private Bitmap getBitmapCache(Bitmap bitmap) {
        Matrix touchMatrix = mainImageView.getImageViewMatrix();

        Bitmap resultBit = Bitmap.createBitmap(bitmap).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(resultBit);

        float[] data = new float[9];
        touchMatrix.getValues(data);
        Matrix3 cal = new Matrix3(data);
        Matrix3 inverseMatrix = cal.inverseMatrix();
        Matrix m = new Matrix();
        m.setValues(inverseMatrix.getValues());

        float[] f = new float[9];
        m.getValues(f);
        int dx = (int) f[Matrix.MTRANS_X];
        int dy = (int) f[Matrix.MTRANS_Y];
        float scale_x = f[Matrix.MSCALE_X];
        float scale_y = f[Matrix.MSCALE_Y];
        canvas.save();
        canvas.translate(dx, dy);
        canvas.scale(scale_x, scale_y);

        photoEditorView.setDrawingCacheEnabled(true);
        if (photoEditorView.getDrawingCache() != null) {
            canvas.drawBitmap(photoEditorView.getDrawingCache(), 0, 0, null);
        }

        if (photoEditorView.getPaintBit() != null) {
            canvas.drawBitmap(photoEditorView.getPaintBit(), 0, 0, null);
        }

        canvas.restore();
        return resultBit;
    }

    @Override
    public void onFilterSelected(ImageFilter imageFilter) {
        selectedFilter = imageFilter;
        new ApplyFilterTask(new TaskCallback<Bitmap>() {
            @Override
            public void onTaskDone(Bitmap data) {
                if (data != null) {
                    setImageBitmap(data);
                }
            }
        }, Bitmap.createBitmap(mainBitmap)).execute(imageFilter);
    }

    protected void setMode(int mode) {
        if (currentMode != mode) {
            onModeChanged(mode);
        } else {
            mode = Constants.MODE_NONE;
            onModeChanged(mode);
        }
        this.currentMode = mode;
    }

    private void onUndoPressed() {
        photoEditorView.onUndoPressed(lastEditedItemList.get(lastEditedItemList.size() - 1));
        lastEditedItemList.remove(lastEditedItemList.size() - 1);
        // mainBitmap = getUndoBitmap();
        //  mainImage.setImageBitmap(mainBitmap);
        // mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        updateUndoButtonVisibility();
    }
}
