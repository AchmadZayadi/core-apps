package com.sesolutions.ui.common;

import com.sesolutions.ui.music_album.FormFragment;

import java.util.Map;

public class CreateEditCoreForm extends FormFragment {

    @Override
    public Map<String, Object> fetchFormValue() {
        Map<String, Object> map = super.fetchFormValue();
        if (map.containsKey("file_type_photo")) {
            map.put("file_type_image", map.get("file_type_photo"));
            map.remove("file_type_photo");
        }
        return map;
    }

    public static CreateEditCoreForm newInstance(int editAlbum, Map<String, Object> map, String url) {
        CreateEditCoreForm fragment = new CreateEditCoreForm();
        fragment.FORM_TYPE = editAlbum;
        fragment.url = url;
        fragment.map = map;
        fragment.loadWhenVisible = true;
        return fragment;
    }
}
