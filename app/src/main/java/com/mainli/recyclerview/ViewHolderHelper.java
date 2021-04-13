package com.mainli.recyclerview;

import androidx.annotation.IdRes;
import android.util.SparseArray;
import android.view.View;

/**
 * ViewHolder的帮助类
 * Created by MrFeng on 2016/4/15.
 */
public class ViewHolderHelper {
    public static <T extends View> T get(View itemView, SparseArray<View> mViews, @IdRes int id, Class<T> viewType) {
        View view = mViews.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mViews.put(id, view);
        }
        if (view == null) {
            return null;
        }
        try {
            //noinspection unchecked
            return (T) view;
        } catch (ClassCastException e) {
            return null;
        }
    }
}