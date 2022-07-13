package com.droidninja.imageeditengine;

public class Constants {

    public static final String FILTER_ORIGINAL = "None";
    public static final String FILTER_INSTAFIX = "Instafix";
    public static final String FILTER_ANSEL = "Ansel";
    public static final String FILTER_TESTINO = "Testino";
    public static final String FILTER_XPRO = "XPro";
    public static final String FILTER_RETRO = "Retro";
    public static final String FILTER_BW = "B&W";
    public static final String FILTER_SEPIA = "Sepia";
    public static final String FILTER_CYANO = "Cyano";
    public static final String FILTER_GEORGIA = "Georgia";
    public static final String FILTER_SAHARA = "Sahara";
    public static final String FILTER_HDR = "HDR";



    public static final int MODE_NONE = 0;
    public static final int MODE_PAINT = 1;
    public static final int MODE_ADD_TEXT = 2;
    public static final int MODE_STICKER = 3;

    public static final int TASK_WALLPAPER = 4;
    public static final int TASK_STICKER = 5;
    public static final int TASK_FONT = 6;
    public static final int TASK_HIDE_CAPTION = 7;
    public static final int TASK_SHOW_CAPTION = 8;
    public static final int TASK_CROP = 9;
    public static final int TASK_DONE = 10;
    public static final int TASK_CAPTION = 11;

    public static class Events {
        public static final int STICKER_MORE = 600;
        public static final int VIEW_EDITED = 601;
        public static final int MODE_CHANGE = 602;
        public static final int TASK = 604;

        public static final int HIDE_BOTTOM_SHEET = 605;
        public static final int FONT = 606;
        public static final int STICKER = 607;
        public static final int DONE = 608;
    }

    public static final int TYPE_PAINT = 101;
    public static final int TYPE_TEXT = 102;
    public static final int TYPE_STICKER = 103;
}
