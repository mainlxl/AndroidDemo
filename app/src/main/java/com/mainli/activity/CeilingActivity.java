package com.mainli.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mainli.recyclerview.StickyGridLayoutManager;
import com.seekting.demo_lib.Demo;

import java.util.ArrayList;
import java.util.List;

@Demo(title = "RecyclerView吸顶", group = {"UI"})
public class CeilingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyListAdapter quickAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = new RecyclerView(this);
        setContentView(recyclerView);
        int[] stickyItemTypes = new int[]{ItemType.VIEW_TYPE_GROUP, //此类型需要吸顶
                ItemType.VIEW_TYPE_GROUP_ICON //此类型需要吸顶
        };
        StickyGridLayoutManager layout = new StickyGridLayoutManager(this, stickyItemTypes, 2);
        quickAdapter = new MyListAdapter();
        layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return quickAdapter.getItemViewType(position) == ItemType.VIEW_TYPE_ITEM ? 1 : 2;
            }
        });
        recyclerView.setLayoutManager(layout);
        quickAdapter.submitList(createFakeDatas());
        recyclerView.setAdapter(quickAdapter);
    }

    private List<DataModel> createFakeDatas() {
        List<DataModel> list = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            DataModel child = new ItemDataModel("第" + 0 + "组第" + (i + 1) + "号");
            list.add(child);
        }
        for (int g = 0; g < 10; g++) {
            DataModel group = (g % 2 == 0) ? new GroupDataModel("第" + (g + 1) + "组") : new GroupDataModelIcon("第" + (g + 1) + "组");
            list.add(group);
            int count = (int) (10 + 10 * Math.random());
            for (int i = 0; i < count; i++) {
                DataModel child = new ItemDataModel("第" + (g + 1) + "组第" + (i + 1) + "号");
                list.add(child);
            }
        }
        return list;
    }

    private interface ItemType {
        int VIEW_TYPE_ITEM = 0;
        int VIEW_TYPE_GROUP = 1;
        int VIEW_TYPE_GROUP_ICON = 2;
    }

    static abstract class DataModel {
        private String name;

        public DataModel(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract int getViewType();
    }

    static class ItemDataModel extends DataModel {
        public ItemDataModel(String name) {
            super(name);
        }

        @Override
        public int getViewType() {
            return ItemType.VIEW_TYPE_ITEM;
        }
    }

    static class GroupDataModel extends DataModel {
        public GroupDataModel(String name) {
            super(name);
        }

        @Override
        public int getViewType() {
            return ItemType.VIEW_TYPE_GROUP;
        }
    }

    static class GroupDataModelIcon extends GroupDataModel {
        public GroupDataModelIcon(String name) {
            super(name);
        }

        @Override
        public int getViewType() {
            return ItemType.VIEW_TYPE_GROUP_ICON;
        }
    }

    static class MyListAdapter extends ListAdapter<DataModel, MyListAdapter.MyViewHolder> {

        public MyListAdapter() {
            super(new DiffCallback());
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView itemView = new TextView(parent.getContext());
            if (viewType == ItemType.VIEW_TYPE_GROUP_ICON) {
                itemView.setBackgroundColor(Color.RED);
                itemView.setMinimumHeight(200);
            } else if (viewType == ItemType.VIEW_TYPE_GROUP) {
                itemView.setBackgroundColor(Color.GRAY);
                itemView.setMinimumHeight(150);
            } else {
                itemView.setBackgroundColor(Color.WHITE);
                itemView.setMinimumHeight(100);
            }
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            DataModel item = getItem(position);
            holder.bind(item.getName());
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getViewType();
        }

        static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
            }

            public void bind(String item) {
                ((TextView) itemView).setText(item);
            }

            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        }


    }

    static class DiffCallback extends DiffUtil.ItemCallback<DataModel> {

        @Override
        public boolean areItemsTheSame(@NonNull DataModel oldItem, @NonNull DataModel newItem) {
            // 这里根据你的需求来判断什么情况下认为是同一个item
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull DataModel oldItem, @NonNull DataModel newItem) {
            // 这里根据你的需求来判断什么情况下认为item的内容没有发生变化
            return oldItem.getName().equals(newItem.getName());
        }
    }


}
