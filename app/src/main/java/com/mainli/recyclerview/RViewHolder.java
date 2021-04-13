package com.mainli.recyclerview;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by lixiaoliang on 2016/4/13.
 * RecyclerView - ViewHolder
 */
public class RViewHolder extends RecyclerView.ViewHolder {
    public static final int viewSizeUndefined = -1;
    private SparseArray<View> mViews;

    public RViewHolder(View itemView, int viewSize) {
        super(itemView);
        mViews = new SparseArray<View>(viewSize > 0 ? viewSize : 10);
    }

    public <T extends View> T getView(@IdRes int id) {
        return get(id, null);
    }

    public <T extends View> T get(@IdRes int id, Class<T> viewType) {
        return ViewHolderHelper.get(itemView, mViews, id, viewType);
    }

    /* package */ int countView() {
        return mViews.size();
    }

    //------------------------------辅助方法---------------------------------------------------------------------
    public void setText(@IdRes int id, CharSequence text) {
        get(id, TextView.class).setText(text);
    }

    public void setOnClickListenr(@IdRes int id, View.OnClickListener linstener) {
        getView(id).setOnClickListener(linstener);
    }

    public void setOnClickListenr(@IdRes int id, Object tag, View.OnClickListener linstener) {
        View view = getView(id);
        view.setTag(tag);
        view.setOnClickListener(linstener);
    }

    public void setOnLongClickListener(@IdRes int id, View.OnLongClickListener linstener) {
        getView(id).setOnLongClickListener(linstener);
    }

}