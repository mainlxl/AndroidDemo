package com.mainli.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.seekting.demo_lib.Demo;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.android.RenderMode;

@Demo(title = "flutter")
public class TestFlutterActivity extends FlutterActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通过FlutterView引入Flutter编写的页面
//        FlutterView flutterView = new FlutterView(this, FlutterView.RenderMode.texture);
//        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        FrameLayout flContainer = findViewById(android.R.id.content);
//        flContainer.addView(flutterView, lp);
//        flutterView.attachToFlutterEngine(getFlutterEngine());
    }
}
