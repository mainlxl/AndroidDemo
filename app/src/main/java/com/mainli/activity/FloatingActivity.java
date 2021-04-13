package com.mainli.activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mainli.service.FloatingService;
import com.seekting.demo_lib.Demo;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 需要权限<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
 */
@Demo(title = "悬浮窗", group = {"UI"})
public class FloatingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        Button button1 = new Button(this);
        button1.setText("启动悬浮窗");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFloatingService(v);
            }
        });
        Button button2 = new Button(this);
        button2.setText("关闭悬浮窗");
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFloatingService(v);
            }
        });
        linearLayout.addView(button1);
        linearLayout.addView(button2);
        setContentView(linearLayout);
    }

    public void startFloatingService(View view) {
        if (! checkFloatPermission(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            startService(new Intent(FloatingActivity.this, FloatingService.class));
        }
    }


    public void closeFloatingService(View view) {
        stopService(new Intent(FloatingActivity.this, FloatingService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (!checkFloatPermission(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(FloatingActivity.this, FloatingService.class));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            AppOpsManager opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            return opsManager.checkOp(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName()) == AppOpsManager.MODE_ALLOWED;
        }
        return true;
    }

}
