package com.mainli.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;

import com.mainli.R;
import com.mainli.recyclerview.RViewHolder;
import com.mainli.recyclerview.RecyclerAdapter;
import com.seekting.demo_lib.Demo;

import java.util.Arrays;

/**
 * Created by lixiaoliang on 2018-5-3.
 */
@Demo(title = "CoordinatorLayout自定义Behavior", group = {"UI"})
public class CoordinatorLayoutActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new RecyclerAdapter<String>(Arrays.asList("aaa", "bb", "ss", "asa", "jhhh", "aaa", "bb", "ss", "asa", "jhhh", "aaa", "bb", "ss", "asa", "jhhh", "aaa", "bb", "ss", "asa", "jhhh", "aaa", "bb", "ss", "asa", "jhhh", "aaa", "bb", "ss", "asa", "jhhh", "aaa", "bb", "ss", "asa", "jhhh", "aaa", "bb", "ss", "asa", "jhhh", "aaa", "bb", "ss", "asa", "jhhh", "aaa", "bb", "ss", "asa", "jhhh")//
                , android.R.layout.activity_list_item) {
            @Override
            public void onBindObject2View(RViewHolder vh, String o, int position) {
                vh.get(android.R.id.icon, ImageView.class).setImageResource(R.mipmap.ic_launcher);
                vh.setText(android.R.id.text1, o);
            }
        });
    }
}
