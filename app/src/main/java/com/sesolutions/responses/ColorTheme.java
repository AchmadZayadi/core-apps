package com.sesolutions.responses;

/**
 * Created by root on 3/1/18.
 */

public class ColorTheme {
    // public static String json = "{ \"test_style_1\":{ \"textColor\":\"#87c33d\", },  \"test_style_2\":{    \"textColor\":\"#ee9a14\",  },  \"test_style_light\":{   },   \"sampleStyle2\":{    \"borderRadius\":\"15\",     \"textColor\":\"#000000\",   \"borderWidth\":\"3\", \"textSize\":\"16\",     \"borderColor\":\"#0075B0\"   }}";
    //public static String json = "{  \"text_style_1\":{  \"textColor\": \"#87c33d\" },   \" text_style_2\":{     \"textColor\": \"#ee9a14\" },   \" text_style_light\":{  \"textColor \": \"#ffffff\"  },    \"sampleStyle2\":{     \"borderRadius\": \"15 \",      \"textColor\": \"#000000\",    \"borderWidth\": \"3 \",  \"textSize\": \"16\",      \"borderColor \": \"#0075B0\"   }}";

    public Text_style_1 text_style_1;
    public Text_style_1 text_style_2;
    public Text_style_1 text_style_toolbar;
    public image_star button_image_toolbar;
    public Text_style_1 text_style_no_data;
    public Text_style_1 text_style_primary;
    public Text_style_1 text_style_light;
    public Text_style_1 text_style_stats;
    public Text_style_1 hint;
    public Card_style card_style;
    public Card_style Card_style_toolbar;
    public Main_back main_style;
    public Main_back foreground_style;
    public Main_back background_style;
    public Main_back divider;
    public toolbar toolbar;
    public button button_simple;
    public image_star image_star;
    public image_star image_tint;
    public image_star image_tint_2;
    public image_star image_tint_primary;
    public tab_layout tab_layout;
    public dialog_style dialog_style;
    public Edit_text_style Edit_text_style;
    public Edit_text_style Edit_text_style_toolbar;


    public static class Text_style_1 {
        final String textColor;

        public Text_style_1(String textColor) {
            this.textColor = textColor;
        }

    }

    public static class Edit_text_style {
        final String editTextColor;
        final String editHintColor;

        public Edit_text_style(String textColor, String hintColor) {
            this.editTextColor = textColor;
            this.editHintColor = hintColor;
        }

    }


    public static class Card_style {
        final String cardColor;

        public Card_style(String cardColor) {
            this.cardColor = cardColor;
        }

    }

    public static class button {
        final String buttonColor;
        final String buttonBack;

        public button(String buttonColor, String buttonBack) {
            this.buttonColor = buttonColor;
            this.buttonBack = buttonBack;
        }
    }

    public static class toolbar {
        final String toolbar;

        public toolbar(String cardColor) {
            this.toolbar = cardColor;
        }
    }

    public static class Main_back {
        final String mainBack;

        public Main_back(String main_back) {
            this.mainBack = main_back;
        }
    }


    public static class image_star {
        final String tint;

        public image_star(String main_back) {
            this.tint = main_back;
        }
    }

    public static class tab_layout {
        final String tabLayout;

        public tab_layout(String tabLayout) {
            this.tabLayout = tabLayout;
        }
    }

    public static class dialog_style {
        final String bgColor;
        public int borderRadius = 10;

        public dialog_style(String bgColor) {
            this.bgColor = bgColor;
        }
    }

   /* public static class Divider {
        private String divider;

        public Divider(String divider) {
            this.divider = divider;
        }

        public String getCardColor() {
            return divider;
        }
    }*/
}
