package com.sesolutions.listeners;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface CustomListAdapterInterface {
     View getView(int position, View convertView, ViewGroup parent, int resourceID, LayoutInflater inflater);
}
