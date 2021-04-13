package com.mainli.recyclerview;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lixiaoliang on 2016/5/14.
 */
public abstract class OnRecyclerViewItemClickListener<T> extends GestureDetector.SimpleOnGestureListener implements RecyclerView.OnItemTouchListener {
    private GestureDetectorCompat mGestureDetectorCompat;
    private RecyclerView mRecyclerView;

    public OnRecyclerViewItemClickListener(RecyclerView recyclerView) {
        if (recyclerView.getAdapter() == null || !(recyclerView.getAdapter() instanceof RecyclerAdapter))
            throw new IllegalStateException("Adapter must exist and Must be com.mainli.adapterlib.recyclerView.RecyclerAdapter Adapter");
        this.mRecyclerView = recyclerView;
        mGestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), this);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        View childViewUnder = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (childViewUnder != null) {
            RViewHolder viewHolder = (RViewHolder) mRecyclerView.findContainingViewHolder(childViewUnder);
            int childAdapterPosition = mRecyclerView.getChildAdapterPosition(childViewUnder);
            RecyclerAdapter<T> adapter = (RecyclerAdapter) mRecyclerView.getAdapter();
            T item = adapter.getItem(childAdapterPosition);
            onItemClick(viewHolder, item, childAdapterPosition);
        }
        return super.onSingleTapUp(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        View childViewUnder = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (childViewUnder != null) {
            RViewHolder viewHolder = (RViewHolder) mRecyclerView.findContainingViewHolder(childViewUnder);
            int childAdapterPosition = mRecyclerView.getChildAdapterPosition(childViewUnder);
            RecyclerAdapter<T> adapter = (RecyclerAdapter) mRecyclerView.getAdapter();
            T item = adapter.getItem(childAdapterPosition);
            onItemLongClick(viewHolder, item, childAdapterPosition);
        }
        super.onLongPress(e);
    }

    /*单击*/
    public abstract void onItemClick(RViewHolder vh, T t, int position);

    /*长按*/
    public void onItemLongClick(RViewHolder vh, T t, int position) {

    }
}
