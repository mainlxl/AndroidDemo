package com.mainli.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.mainli.R;
import com.mainli.annotations.BindView;
import com.seekting.demo_lib.Demo;


/**
 * Created by Mainli on 2018-3-26.
 * 日志文件 放在D:\processor-log.txt
 */
@Demo(title = "自定义编译时注解",group = {"框架"})
public class TestAPTActivity extends AppCompatActivity {
    @BindView(R.id.bottom)
    TextView tx;
    @BindView(88888)
    TextView helloWorld;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText(com.mainli.processor.Log.log);
        setContentView(textView);
    }
}
