package com.sesolutions.imageeditengine;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;

public class ImageEditor {
    public static final int EDITOR_STICKER = 1;
    public static final int EDITOR_TEXT = 2;
    public static final int EDITOR_PAINT = 3;
    public static final int EDITOR_CROP = 4;
    public static final int EDITOR_FILTERS = 5;
    public static final int TYPE_WALLPAPER_MORE = 6;
    public static final int TYPE_WALLPAPER_GALLERY = 7;

    public static final String EXTRA_STICKER_FOLDER_NAME = "EXTRA_STICKER_FOLDER_NAME";
    public static final String EXTRA_IS_TEXT_MODE = "EXTRA_IS_TEXT_MODE";
    public static final String EXTRA_IS_PAINT_MODE = "EXTRA_IS_PAINT_MODE";
    public static final String EXTRA_IS_STICKER_MODE = "EXTRA_IS_STICKER_MODE";
    public static final String EXTRA_IS_CROP_MODE = "EXTRA_IS_CROP_MODE";
    public static final String EXTRA_HAS_FILTERS = "EXTRA_HAS_FILTERS";
    public static final String EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH";
    public static final String EXTRA_IMAGE_PATH_LIST = "EXTRA_IMAGE_PATH_LIST";
    public static final String EXTRA_ORIGINAL = "EXTRA_ORIGINAL";
    public static final String EXTRA_CROP_RECT = "EXTRA_CROP_RECT";
    public static final String EXTRA_QUOTE_TITLE = "EXTRA_QUOTE_TITLE";

    public static final String EXTRA_EDITED_PATH = "EXTRA_EDITED_PATH";

    public static final int RC_IMAGE_EDITOR = 0x34;
    public static final String EXTRA_QUOTE_SOURCE = "EXTRA_QUOTE_SOURCE";
    public static final String EXTRA_IS_WALLPAPER = "EXTRA_WALLPAPER";
    public static final String EXTRA_SHOW_DONE_BUTTON = "EXTRA_DONE_BUTTON";
    public static final String EXTRA_BACK_BUTTON = "EXTRA_BACK_BUTTON";


    public static class Builder {

        private String imagePath;
        //private ArrayList<String> imagePathList;
        private Activity context;
        private String stickerFolderName;
        private boolean enabledEditorText = true;
        private boolean enabledEditorPaint = true;
        private boolean enabledEditorSticker = true;
        private boolean enableEditorCrop = true;
        private boolean enableFilters = true;
        private String title, source;

        public Builder(Activity context, String imagePath) {
            this.context = context;
            this.imagePath = imagePath;
        }

        public Builder(Activity context/*, ArrayList<String> imagePathList*/) {
            this.context = context;
            // this.imagePathList = imagePathList;
        }

        public Builder setStickerAssets(String folderName) {
            this.stickerFolderName = folderName;
            enabledEditorSticker = true;
            return this;
        }

        public Builder disable(int editorType) {
            if (editorType == EDITOR_TEXT) {
                enabledEditorText = false;
            } else if (editorType == EDITOR_PAINT) {
                enabledEditorPaint = false;
            } else if (editorType == EDITOR_STICKER) {
                enabledEditorSticker = false;
            } else if (editorType == EDITOR_CROP) {
                enableEditorCrop = false;
            } else if (editorType == EDITOR_FILTERS) {
                enableFilters = false;
            }

            return this;
        }

        public Intent getEditorIntent() {
            if (imagePath != null && (new File(imagePath).exists())) {
                Intent intent = new Intent(context, ImageEditActivity.class);
                intent.putExtra(ImageEditor.EXTRA_STICKER_FOLDER_NAME, stickerFolderName);
                intent.putExtra(ImageEditor.EXTRA_IS_PAINT_MODE, enabledEditorPaint);
                intent.putExtra(ImageEditor.EXTRA_IS_STICKER_MODE, enabledEditorSticker);
                intent.putExtra(ImageEditor.EXTRA_IS_TEXT_MODE, enabledEditorText);
                intent.putExtra(ImageEditor.EXTRA_IS_CROP_MODE, enableEditorCrop);
                intent.putExtra(ImageEditor.EXTRA_HAS_FILTERS, enableFilters);
                intent.putExtra(ImageEditor.EXTRA_IMAGE_PATH, imagePath);
                intent.putExtra(ImageEditor.EXTRA_QUOTE_TITLE, title);
                intent.putExtra(ImageEditor.EXTRA_IS_WALLPAPER, true);
                intent.putExtra(ImageEditor.EXTRA_SHOW_DONE_BUTTON, true);
                intent.putExtra(ImageEditor.EXTRA_BACK_BUTTON, true);
                intent.putExtra(ImageEditor.EXTRA_QUOTE_SOURCE, source);
                return intent;
                //context.startActivityForResult(intent, RC_IMAGE_EDITOR);
            } else {
                Toast.makeText(context, "Invalid image path", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        public Intent getMultipleEditorIntent() {
            Intent intent = new Intent(context, MultipleImageEditActivity.class);
            intent.putExtra(ImageEditor.EXTRA_STICKER_FOLDER_NAME, stickerFolderName);
            intent.putExtra(ImageEditor.EXTRA_IS_PAINT_MODE, enabledEditorPaint);
            intent.putExtra(ImageEditor.EXTRA_IS_STICKER_MODE, enabledEditorSticker);
            intent.putExtra(ImageEditor.EXTRA_IS_TEXT_MODE, enabledEditorText);
            intent.putExtra(ImageEditor.EXTRA_IS_CROP_MODE, false);
            intent.putExtra(ImageEditor.EXTRA_IS_WALLPAPER, false);
            intent.putExtra(ImageEditor.EXTRA_SHOW_DONE_BUTTON, false);
            intent.putExtra(ImageEditor.EXTRA_BACK_BUTTON, false);
            intent.putExtra(ImageEditor.EXTRA_HAS_FILTERS, enableFilters);
            // intent.putStringArrayListExtra(ImageEditor.EXTRA_IMAGE_PATH_LIST, imagePathList);
            return intent;
            //context.startActivityForResult(intent, RC_IMAGE_EDITOR);
        }

        public Builder setQuote(String title) {
            this.title = title;
            return this;
        }

        public Builder setQuoteSource(String source) {
            this.source = source;
            return this;
        }
    }
}
