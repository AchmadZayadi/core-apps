package com.sesolutions.ui.music_album;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.rey.material.widget.Slider;
import com.sesolutions.R;
import com.sesolutions.animate.DetailsTransition;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.musicplayer.MusicService;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicListFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, SeekBar.OnSeekBarChangeListener {

    private View v;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private List<Albums> albumList;
    private int selectedPosition;
    private MusicService mService;
    private ImageView ivPlay;
    private ImageView ivNext;
    private ImageView ivPrev;
    private ImageView ivShuffle;
    private ImageView ivRepeat;
    private ProgressBar pb;
    private com.rey.material.widget.Slider seekbar;

    // private OnUserClickedListener<Integer, String> previousListener;
    private int colorPrimary;
    private TextView tvTitle;
    private TextView tvElapsed;
    private TextView tvTotalTime;

    private TextView tvSongTitleUpperDetail;
    private TextView tvDetailSongTitle;
    private Typeface iconFont;

    private Drawable dPlay;
    private Drawable dPause;
    private TextView tvLyrics;
    private TextView tvClear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_list, container, false);
        getActivity().getWindow().setStatusBarColor(Color.parseColor("#000000"));

        try {
            new ThemeManager().applyTheme((ViewGroup) v, context);
            init();
            setupViewPager();
            ivShuffle.setColorFilter(mService.isShuffeled() ? colorPrimary : Color.WHITE);
            ivRepeat.setColorFilter(mService.isRepeat() ? colorPrimary : Color.WHITE);
            v.findViewById(R.id.ivOption).setOnClickListener(this);
            updatePlayIcon();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void updatePlayIcon() {
        try {
            ivPlay.setImageDrawable(mService.isPng() ? dPause : dPlay);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setupViewPager() {
        albumList = new ArrayList<>();
        mService = ((MainApplication) activity.getApplication()).getMusicService();
        albumList.addAll(mService.getSongList());
        selectedPosition = mService.getCurrentSongPosition();
        adapter = new ViewPagerAdapter(fragmentManager, albumList);
        viewPager.setAdapter(adapter);
        //  viewPager.setOffscreenPageLimit(6);
        tvTitle.setText(albumList.get(selectedPosition).getTitle());
        viewPager.setCurrentItem(selectedPosition);

        Glide.with(context).load(albumList.get(selectedPosition).getImageUrl()).into((ImageView) v.findViewById(R.id.backrl));

/*
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // tvTitle.setText(albumList.get(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    private ImageView ivSongImageDetail;
    private TextView tvSongTitleDetail;
    private TextView tvDetailAlbumTitle;
    private TextView tvImagePlaylistDetail;
    private TextView tvImageAlbumDetail;
    private TextView tvImageArtistDetail;
    private TextView tvImageSongDetail;
    private TextView tvDetailArtistTitle;

    private void setDetaiLayout() {
        try {
            Albums vo = albumList.get(viewPager.getCurrentItem());
            CustomLog.e("albumVo", "" + new Gson().toJson(vo));
            Util.showImageWithGlide(ivSongImageDetail, vo.getImageUrl(), context);
            tvSongTitleDetail.setText(vo.getTitle());
            if (!TextUtils.isEmpty(vo.getAlbumTitle())) {
                tvImageAlbumDetail.setTypeface(iconFont);
                tvImageAlbumDetail.setText(Constant.FontIcon.ALBUM);
                tvDetailAlbumTitle.setText(vo.getAlbumTitle() + " >");
            } else {
                v.findViewById(R.id.rlDetailAlbum).setVisibility(View.GONE);
            }

            if (null != vo.getArtists() && vo.getArtists().size() > 0) {
                tvImageArtistDetail.setTypeface(iconFont);
                tvImageArtistDetail.setText(Constant.FontIcon.ARTIST);
                tvDetailArtistTitle.setText(addClickableArtist(vo.getArtists()));
                tvDetailArtistTitle.setMovementMethod(LinkMovementMethod.getInstance());
                // tvDetailAlbumTitle.setText(vo.getName() + " >");
            } else {
                v.findViewById(R.id.rlDetailArtist).setVisibility(View.GONE);
            }

            if (vo.getSongId() > 0) {
                tvImageSongDetail.setTypeface(iconFont);
                tvImageSongDetail.setText(Constant.FontIcon.ARTIST);
                tvDetailSongTitle.setText(vo.getTitle() + " >");
            } else {
                v.findViewById(R.id.rlDetailSong).setVisibility(View.GONE);
            }

            tvImagePlaylistDetail.setTypeface(iconFont);
            tvImagePlaylistDetail.setText(Constant.FontIcon.PLAYLIST);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void init() {
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //ivProfileImage = v.findViewById(R.id.ivProfileImage);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setSelected(true);
        viewPager = v.findViewById(R.id.vpSong);
        ivPlay = v.findViewById(R.id.ivPlay);
        pb = v.findViewById(R.id.pb);
        ivNext = v.findViewById(R.id.ivNext);
        ivPrev = v.findViewById(R.id.ivPrev);
        tvElapsed = v.findViewById(R.id.tvStartTime);
        tvTotalTime = v.findViewById(R.id.tvTotalTime);
        ivShuffle = v.findViewById(R.id.ivShuffle);
        ivRepeat = v.findViewById(R.id.ivRepeat);
        seekbar = v.findViewById(R.id.seekbar);

        seekbar.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {

            }
        });
     //   seekbar.setOnSeekBarChangeListener(this);
        ivSongImageDetail = v.findViewById(R.id.ivSongImageDetail);
        tvSongTitleDetail = v.findViewById(R.id.tvSongTitleDetail);
        tvImagePlaylistDetail = v.findViewById(R.id.tvImagePlaylistDetail);
        tvImageAlbumDetail = v.findViewById(R.id.tvImageAlbumDetail);
        tvImageArtistDetail = v.findViewById(R.id.tvImageArtistDetail);
        tvImageSongDetail = v.findViewById(R.id.tvImageSongDetail);
        tvDetailArtistTitle = v.findViewById(R.id.tvDetailArtistTitle);

        colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary);

        v.findViewById(R.id.ivOption).setOnClickListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivShuffle.setOnClickListener(this);
        ivRepeat.setOnClickListener(this);
        tvClear = v.findViewById(R.id.tvClear);
        tvClear.setTypeface(iconFont);
        tvClear.setOnClickListener(this);
        tvLyrics = v.findViewById(R.id.tvLyrics);
        tvLyrics.setTypeface(iconFont);
        tvLyrics.setOnClickListener(this);


        dPlay = ContextCompat.getDrawable(context, R.drawable.play_button2);
        dPause = ContextCompat.getDrawable(context, R.drawable.pause);
        //initSlide();

        tvSongTitleUpperDetail = v.findViewById(R.id.tvSongTitleUpperDetail);
        tvDetailAlbumTitle = v.findViewById(R.id.tvDetailAlbumTitle);
        tvDetailSongTitle = v.findViewById(R.id.tvDetailSongTitle);


        v.findViewById(R.id.tvClearDetail).setOnClickListener(this);
        v.findViewById(R.id.ivClearDetail).setOnClickListener(this);
        v.findViewById(R.id.bCancelDetail).setOnClickListener(this);
        v.findViewById(R.id.rlDetailPlaylist).setOnClickListener(this);
        v.findViewById(R.id.tvDetailAlbumTitle).setOnClickListener(this);
        v.findViewById(R.id.tvSongTitleDetail).setOnClickListener(this);
        v.findViewById(R.id.tvDetailArtistTitle).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            ((CommonActivity) activity).hideMusicLayout();
        /*if (previousListener == null) {
            previousListener = mService.getProgressListener();
            mService.removeAllListeners();
        }*/
            mService.setProgressListener(Constant.Listener.MUSIC_LIST, this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((CommonActivity) activity).hideMusicLayout();
            mService.setProgressListener(Constant.Listener.MUSIC_LIST, this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onBackPressed() {
        if (v.findViewById(R.id.rlMainDetail).getVisibility() != View.VISIBLE) {
            ((CommonActivity) activity).showMusicLayout();
            super.onBackPressed();
        } else {
            v.findViewById(R.id.rlMainDetail).setVisibility(View.GONE);
        }
    }

    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivNext:
                    ((CommonActivity) activity).playNext();
                    break;

                case R.id.tvClear:
                    mService.removeListener(Constant.Listener.MUSIC_LIST);
                    ((CommonActivity) activity).stopMusicPlayer();
                    super.onBackPressed();
                    break;

                case R.id.tvLyrics:
                    openLyricsFragment();
                    break;

                case R.id.ivPlay:
                    if (((CommonActivity) activity).isPlaying()) {
                        ivPlay.setImageDrawable(dPause);
                        ((CommonActivity) activity).pause();
                    } else {
                        ivPlay.setImageDrawable(dPlay);
                        ((CommonActivity) activity).start();
                    }
                    break;
                case R.id.ivPrev:
                    ((CommonActivity) activity).playPrev();
                    break;
                case R.id.ivShuffle:
                    mService.setShuffle();
                    ivShuffle.setColorFilter(mService.isShuffeled() ? colorPrimary : Color.WHITE);

                    break;

                case R.id.ivOption:
                    v.findViewById(R.id.rlMainDetail).setVisibility(View.VISIBLE);
                    setDetaiLayout();
                    break;
                case R.id.bCancelDetail:
                    v.findViewById(R.id.rlMainDetail).setVisibility(View.GONE);
                    break;

                case R.id.tvSongTitleDetail:
                    goToSongsView(getSongId());
                    break;


                case R.id.tvDetailAlbumTitle:
                    goToAlbumView(getAlbumId());
                    break;

                case R.id.tvClearDetail:
                    clearCurrent();
                    break;
                case R.id.ivClearDetail:
                    showShareDialog(viewPager.getCurrentItem());
                    break;

                case R.id.ivRepeat:
                    mService.setRepeat();
                    ivRepeat.setColorFilter(mService.isRepeat() ? colorPrimary : Color.WHITE);
                    break;

                case R.id.rlDetailPlaylist:
                    openAddPlaylistForm(getSongId());
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void openLyricsFragment() {
        String lyrics = albumList.get(viewPager.getCurrentItem()).getLyrics();
        if (TextUtils.isEmpty(lyrics)) {
            Util.showSnackbar(v, Constant.MSG_NO_LYRICS);
            return;
        }
        ShowLyricsFragment fragment = ShowLyricsFragment.newInstance(lyrics, Constant.TXT_TITLE_LYRICS, false);
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fragment.setSharedElementEnterTransition(new DetailsTransition());
                fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                fragment.setExitTransition(new Slide(Gravity.BOTTOM));
            /*    fragment.setEnterTransition(new Explode());
                fragment.setExitTransition(new Explode());*/
                fragment.setAllowEnterTransitionOverlap(true);
                fragment.setAllowReturnTransitionOverlap(false);
                fragment.setSharedElementReturnTransition(new DetailsTransition());
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    // .addSharedElement(cvLogin, res.getString(R.string.login_card))
                    //.addSharedElement(ivUserImage, res.getString(R.string.user_image))
                    //.addSharedElement(tvUserName, res.getString(R.string.username))
                    //     .addSharedElement(ivMobile, res.getString(R.string.login_mobile))
                    //    .addSharedElement(ivPassword, res.getString(R.string.login_password))
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            CustomLog.e(e);
            CustomLog.e("TRANSITION_ERROR", "Build.VERSION.SDK_INT =" + Build.VERSION.SDK_INT);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void clearCurrent() {
        try {
            if (albumList.size() > 1) {
                albumList.remove(viewPager.getCurrentItem());
                adapter.notifyDataSetChanged();
                selectedPosition = mService.getCurrentSongPosition();
                viewPager.setCurrentItem(selectedPosition);
                onBackPressed();
                ((CommonActivity) activity).removeSong(viewPager.getCurrentItem());
            } else {
                mService.removeListener(Constant.Listener.MUSIC_LIST);
                ((CommonActivity) activity).stopMusicPlayer();
                super.onBackPressed();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private int getSongId() {
        return albumList.get(viewPager.getCurrentItem()).getSongId();
    }

    private int getAlbumId() {
        return albumList.get(viewPager.getCurrentItem()).getAlbumId();
    }


    private void openAddPlaylistForm(int songId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_SONG_ID, songId);

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        AddToPlaylistFragment.newInstance(Constant.FormType.TYPE_ADD_SONG,
                                map, Constant.URL_CREATE_PLAYLIST
                        ))
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onStop() {
        try {
            mService.removeListener(Constant.Listener.MUSIC_LIST);
            if (Util.isServiceRunning(MusicService.class.getName(), context)) {
                if (null != mService.getSongList() && mService.getSongList().size() > 0)
                    ((CommonActivity) activity).showMusicLayout();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        super.onStop();
    }

    @Override
    public boolean onItemClicked(Integer object1, Object value, int postion) {
        try {
            switch (object1) {
                case Constant.Events.MUSIC_PROGRESS:
                    seekbar.setValue(postion,false);
                    tvTotalTime.setText(("" + value).split("@")[0]);
                    tvElapsed.setText(("" + value).split("@")[1]);
                    break;
                case Constant.Events.MUSIC_CHANGED:
                    ivPlay.setVisibility(View.INVISIBLE);
                    pb.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(postion);
                    tvTitle.setText(albumList.get(postion).getTitle());
                    break;
                case Constant.Events.MUSIC_PREPARED:
                    ivPlay.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    ivPlay.setImageDrawable(dPause);
                    break;
                case Constant.Events.PLAY:
                    ivPlay.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    ivPlay.setImageDrawable(dPause);
                    break;
                case Constant.Events.PAUSE:
                    ivPlay.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    ivPlay.setImageDrawable(dPlay);
                    break;
                case Constant.Events.STOP:
                    mService.removeListener(Constant.Listener.MUSIC_LIST);
                    ((CommonActivity) activity).stopMusicPlayer();
                    super.onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void showShareDialog(final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_three);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.TXT_SHARE_FEED);
            AppCompatButton bShareIn = progressDialog.findViewById(R.id.bCamera);
            AppCompatButton bShareOut = progressDialog.findViewById(R.id.bGallary);
            bShareOut.setText(Constant.TXT_SHARE_OUTSIDE);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
            bShareIn.setText(Constant.TXT_SHARE_INSIDE + AppConfiguration.SHARE);
            bShareIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareInside(albumList.get(position).getShare(), activity instanceof CommonActivity);

                }
            });

            bShareOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareOutside(albumList.get(position).getShare());
                }
            });

            progressDialog.findViewById(R.id.bLink).setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            ((CommonActivity) activity).seekTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mService.removeListener(Constant.Listener.MUSIC_LIST);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mService.setProgressListener(Constant.Listener.MUSIC_LIST, this);
    }
}
