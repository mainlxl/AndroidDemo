package com.mainli.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mainli.R;
import com.mainli.utils.MarkDownURLMatcher;
import com.mainli.view.LinkedEditText;
import com.seekting.demo_lib.Demo;

/**
 * Created by Mainli on 2018-3-28.
 */
@Demo(title = "Emoji表情", group = {"View"})
public class RichMediaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_media);
        final LinkedEditText et = findViewById(R.id.et);
        final TextView tv = findViewById(R.id.tv);
        final TextView tv1 = findViewById(R.id.tv1);
        MarkDownURLMatcher.attachTextViewOnTouchClickable(tv1);
        findViewById(R.id.btn_output).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(et.toMDString());
            }
        });
        findViewById(R.id.btn_convert_output).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv1.setText(MarkDownURLMatcher.convertTextLinks(et.toMDString(),false));
            }
        });
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            private int count = 0;

            @Override
            public void onClick(View v) {
                et.insertLinked("链接" + count++, "http://www.baidu.com");
            }
        });
    }

}
