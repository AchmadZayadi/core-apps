package com.sesolutions.listeners;

/**
 * Created by root on 7/11/17.
 */

public interface OnUserClickedListener<T, E> {
    boolean onItemClicked(T eventType, E data, int position);
}
