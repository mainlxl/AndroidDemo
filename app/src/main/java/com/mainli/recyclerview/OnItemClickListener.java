package com.mainli.recyclerview;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lixiaoliang on 2016/5/14.
 */
public abstract class OnItemClickListener extends GestureDetector.SimpleOnGestureListener implements RecyclerView.OnItemTouchListener {
    private GestureDetectorCompat mGestureDetectorCompat;
    private RecyclerView mRecyclerView;

    public OnItemClickListener(RecyclerView recyclerView) {
        if (recyclerView.getAdapter() == null)
            throw new IllegalStateException("Adapter must exist");
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
            onItemClick(viewHolder,childAdapterPosition);
        }else{
            onClickBlank(e);
        }
        return super.onSingleTapUp(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        View childViewUnder = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
        if (childViewUnder != null) {
            RViewHolder viewHolder = (RViewHolder) mRecyclerView.findContainingViewHolder(childViewUnder);
            int childAdapterPosition = mRecyclerView.getChildAdapterPosition(childViewUnder);
            onItemLongClick(viewHolder,childAdapterPosition);
        }
        super.onLongPress(e);
    }

    /*单击*/
    public abstract void onItemClick(RViewHolder vh,int position);

    /*长按*/
    public void onItemLongClick(RViewHolder vh,int position) {

    }
    /*单机空白*/
    public void onClickBlank(MotionEvent e) {

    }
}
