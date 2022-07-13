package me.riddhimanadib.formmaster.model;

import java.util.List;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementMusicFile extends BaseFormElement {

    private OnTextClickListener clickListener;
    private List<String> musicList;

    public FormElementMusicFile() {
    }

    public static FormElementMusicFile createInstance() {
        FormElementMusicFile FormElementSwitch = new FormElementMusicFile();
        FormElementSwitch.setType(BaseFormElement.TYPE_MUSIC_FILE);
        return FormElementSwitch;
    }

    public FormElementMusicFile setTag(int mTag) {
        return (FormElementMusicFile) super.setTag(mTag);
    }

    public FormElementMusicFile setType(int mType) {
        return (FormElementMusicFile) super.setType(mType);
    }

    public FormElementMusicFile setTitle(String mTitle) {
        return (FormElementMusicFile) super.setTitle(mTitle);
    }

    public FormElementMusicFile setValue(String mValue) {
        return (FormElementMusicFile) super.setValue(mValue);
    }

    public FormElementMusicFile setMusicList(List<String> mValue) {
        musicList = mValue;
        return this;
    }

    public List<String> getMusicList() {
        return musicList;
    }

    public FormElementMusicFile setHint(String mHint) {
        return (FormElementMusicFile) super.setHint(mHint);
    }

    public FormElementMusicFile setRequired(boolean required) {
        return (FormElementMusicFile) super.setRequired(required);
    }


    public FormElementMusicFile setClickListener(OnTextClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    // custom getters

    public OnTextClickListener getClickListener() {
        return this.clickListener;
    }


    public boolean isFileSelected() {
        return musicList != null && musicList.size() > 0;
    }
}
