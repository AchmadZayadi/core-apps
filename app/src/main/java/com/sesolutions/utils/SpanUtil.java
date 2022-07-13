package com.sesolutions.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.responses.feed.ActivityType;
import com.sesolutions.responses.feed.Tagged;
import com.sesolutions.responses.videos.Tags;

import java.util.List;

import static android.graphics.Typeface.BOLD;

/**
 * Created by root on 23/11/17.
 */

public class SpanUtil {

    private static BetterImageSpan makeImageSpan(Context context, Drawable drawable, int size) {
        //  final Drawable drawable = context.getResources().getDrawable(drawableResId);
        drawable.mutate();
        drawable.setBounds(0, 0, size, size);
        return new BetterImageSpan(drawable, BetterImageSpan.ALIGN_CENTER);
    }

    public static SpannableString getHashTags(List<Tags> tags, final OnUserClickedListener<Integer, Object> listener) {

        String text = "";
        for (final Tags men : tags) {
            if (!TextUtils.isEmpty(men.getText())) {
                text = text + " #" + men.getText();
            }
        }
        SpannableString span = new SpannableString(text);
        try {

            for (final Tags men : tags) {
                if (!TextUtils.isEmpty(men.getText())) {
                    //  text = text + " #" + men.getText();
                    int startMention = text.lastIndexOf(men.getText()) - 1;
                    int endMention = startMention + men.getText().length() + 1;
                    // body = body.replace(men.getWord(), men.getTitle());

                    span.setSpan(new CustomClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            listener.onItemClicked(Constant.Events.CLICKED_BODY_HASH_TAGGED, "" + men, men.getTagId());
                        }
                    }, startMention, endMention, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    span.setSpan(new StyleSpan(BOLD), startMention, endMention, 0);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return span;
    }

    public static void createHashTagView(LayoutInflater inflater, ViewGroup flowLayout, List<Tags> tags, final OnUserClickedListener<Integer, Object> listener) {
        if (tags != null) {
            flowLayout.setVisibility(View.VISIBLE);
            flowLayout.removeAllViews();
            for (final Tags men : tags) {
                final AppCompatTextView view1 = (AppCompatTextView) inflater.inflate(R.layout.layout_rounded_textview, flowLayout, false);
                view1.setText(men.getText());
                view1.setOnClickListener(v -> listener.onItemClicked(Constant.Events.CLICKED_BODY_HASH_TAGGED, men, men.getTagId()));
                flowLayout.addView(view1);
            }
        } else {
            flowLayout.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    public static String getHtmlString(String value, boolean isNougat) {
        try {
            if (isNougat) {
                return (Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY)).toString();
            } else {
                return (Html.fromHtml(value)).toString();
            }
        } catch (Exception e) {
            //do not change anything in case of Exception
            return value;
        }
    }

    public static String getHtmlString(String value) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                return (Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY)).toString();
            } else {
                return (Html.fromHtml(value)).toString();
            }
        } catch (Exception e) {
            //do not change anything in case of Exception
            return value;
        }
    }

    public static Tagged convertFriendToTagged(Friends vo) {
        Tagged tagged = new Tagged();
        tagged.setImageUrl(vo.getPhoto());
        tagged.setName(vo.getLabel());
        tagged.setUserId(vo.getId());
        return tagged;
    }

    public static Friends convertTaggedToFriend(Tagged vo) {
        Friends fr = new Friends();
        fr.setPhoto(vo.getImageUrl());
        fr.setLabel(vo.getName());
        fr.setId(vo.getUserId());
        return fr;
    }

    public static SpannableString createSpan(String title, Activity vo, final Context context, final TextView tvTitleName, final int emojiSize) {
        //String title = vo.getItemUser().getTitle();
        try {
            final int lenTitle = title.length();
            int startFeelTitle = 0;
            int endFellTitle = 0;
            int startLocation = 0;
            int endLocation = 0;
            int startTag1 = 0;
            int endTag1 = 0;
            int startTag2 = 0;
            int endTag2 = 0;

            // final Drawable[] drawable = new Drawable[1];
            String header = title;
            if (vo.getFeelings() != null) {
                String feeling = vo.getFeelings().is_string().replace(" ", "  ");
                feeling = feeling + " " + (vo.getFeelings().getFeeling_title());
                startFeelTitle = lenTitle + feeling.length() + 1;
                feeling = feeling + " " + vo.getFeelings().getTitle();
                endFellTitle = lenTitle + feeling.length() + 1;
                header = header + " " + feeling;
            }

            if (vo.getTagged() != null && vo.getTagged().size() > 0) {
                header = header + Constant._WITH_;
                startTag1 = header.length();
                String tagged = vo.getTagged().get(0).getName();
                header = header + tagged;
                endTag1 = header.length();
                if (vo.getTagged().size() == 2) {
                    header = header + Constant._AND_;
                    startTag2 = header.length();
                    header = header + vo.getTagged().get(1).getName();
                    endTag2 = header.length();
                } else if (vo.getTagged().size() > 2) {
                    header = header + Constant._AND_;
                    startTag2 = header.length();
                    header = header + (vo.getTagged().size() - 1) + Constant._OTHERS;
                    endTag2 = header.length();
                }

            }


            if (vo.getLocationActivity() != null) {
                String location = vo.getLocationActivity().getVenue();
                header = header + Constant._IN_;
                startLocation = header.length();
                header = header + location;
                endLocation = header.length();
            }

            final SpannableString[] spanArr = new SpannableString[1];

            if (!TextUtils.isEmpty(vo.getActivityTypeContent())) {

                String content = vo.getActivityTypeContent().replace("\r\n", "");
                header = header + content;

                if (vo.getActivityType() != null) {
                    List<ActivityType> actTypelIst = vo.getActivityType();
                    if (actTypelIst.size() > 0) {
                        for (ActivityType type : actTypelIst) {
                            if (content.contains(type.getKey())) {
                                try {
                                    header = header.replace(type.getKey(), type.getTitle());
                                    int start = /*headerLength +*/ header.lastIndexOf(type.getTitle());
                                    int end = start + (type.getTitle().length());
                                    type.setEndIndex(end);
                                    type.setStartIndex(start);
                                } catch (Exception e) {
                                    CustomLog.e("EXCEPTION", "EXCEPTIOn IN JSON KEY IS PRESENT BUT TITLE IS NOT PRESENT ");
                                }
                            }
                        }

                        spanArr[0] = new SpannableString(header);

                        for (final ActivityType type : actTypelIst) {

                            if (type.getStartIndex() > -1) {
                                spanArr[0].setSpan(new StyleSpan(BOLD), type.getStartIndex(), type.getEndIndex(), 0);
                            }
                        }
                    }
                }
            }


            if (spanArr[0] == null) {
                spanArr[0] = new SpannableString(header);
            }


            //For Click
            //   spanArr[0].setSpan(titleSpan, 0, lenTitle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //For Bold
            spanArr[0].setSpan(new StyleSpan(BOLD), 0, lenTitle, 0);
            if (startFeelTitle > 0) {

                Glide.with(context).asBitmap()
                        .load(vo.getFeelings().getIcon())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                CustomLog.e("drawable", "image");
                                spanArr[0].setSpan(makeImageSpan(context, new BitmapDrawable(null, resource), emojiSize), lenTitle + 4, lenTitle + 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                tvTitleName.setText(spanArr[0]);
                            }
                        });
                spanArr[0].setSpan(new StyleSpan(BOLD), startFeelTitle, endFellTitle, 0);
            }
            if (startLocation > 0) {
                spanArr[0].setSpan(new StyleSpan(BOLD), startLocation, endLocation, 0);
            }

            if (startTag1 > 0) {
                spanArr[0].setSpan(new StyleSpan(BOLD), startTag1, endTag1, 0);
            }

            if (startTag2 > 0) {


                // spanArr[0].setSpan(tag2Span, startTag2, endTag2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanArr[0].setSpan(new StyleSpan(BOLD), startTag2, endTag2, 0);
            }

            // holderParent.tvHeader.setText(spanArr[0]);
            // holderParent.tvHeader.setMovementMethod(LinkMovementMethod.getInstance());

            return spanArr[0];
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return null;
    }


}
