package com.sesolutions.ui.drawer;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.sesolutions.listeners.CustomListAdapterInterface;

import java.util.List;


class DrawerAdapter extends ArrayAdapter<DrawerModel.Menus> {

    //private final List<DrawerModel.Menus> list;
    private final int rowlayoutID;
    private final LayoutInflater inflater;
    private final CustomListAdapterInterface ref;

    public DrawerAdapter(Context context, int resource, List<DrawerModel.Menus> objects, CustomListAdapterInterface ref) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        // this.list = objects;
        this.ref = ref;
        this.rowlayoutID = resource;
    }

   /* @Override
    public int getCount() {
        return list.size() + 1;
    }*/

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // TODO Auto-generated method stub
        return ref.getView(position, convertView, parent, rowlayoutID, inflater);
    }

}
