package com.mainli.xscan;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mainli.xscan.core.QRCodeView;
import com.mainli.xscan.core.ScanBoxView;
import com.mainli.xscan.zxing.ZXingView;
import com.seekting.demo_lib.Demo;

@Demo(title = "扫码")
public class XScanActivity extends AppCompatActivity implements QRCodeView.Delegate {
    ////val KEY_RESULT="x_scan_result";
////        val RESULT_CODE_X_SCAN=-1191;
    private static final int PERMISSIONS_REQUEST_CODE_CEATE = 0x123;
    private static final int PERMISSIONS_REQUEST_CODE_RESTART = 0x124;
    private ZXingView mZXingView;

    private Runnable mCreateScanRunable = new Runnable() {
        @Override
        public void run() {
            init();
        }
    };


    private Runnable mReStartScanRunable = new Runnable() {
        @Override
        public void run() {
            startScan();
        }
    };

    private void startScan() {
        if (mZXingView != null) {
            mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
            //mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autoPermission(mCreateScanRunable, PERMISSIONS_REQUEST_CODE_CEATE);
    }

    private void init() {
        XScanActivity.this.setContentView(R.layout.activity_x_scan);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.hide();
        //设置状态栏透明
        setTranslucentStatus();
        mZXingView = (ZXingView) findViewById(R.id.zxingview);
        mZXingView.setDelegate(XScanActivity.this);
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        ScanBoxView scanBoxView = mZXingView.getScanBoxView();
        scanBoxView.setRectWidth((int) (displayMetrics.widthPixels * 2.0f / 3f + 0.5f));
        int rectHeight = scanBoxView.getRectHeight();
        scanBoxView.setTopOffset((displayMetrics.heightPixels - rectHeight) >> 1);
        View toolbar = findViewById(R.id.scan_toolbar);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        layoutParams.topMargin = getStatusBarHeight();
        toolbar.setLayoutParams(layoutParams);
    }

    @Override
    protected void onStart() {
        startScan();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        autoPermission(mReStartScanRunable, PERMISSIONS_REQUEST_CODE_RESTART);
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onResultRun(requestCode);
        } else {
            Toast.makeText(this, "请开启相机权限", Toast.LENGTH_SHORT);
        }
    }

    private void autoPermission(Runnable runnable, int requestCode) {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            runnable.run();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, requestCode);
        }
    }

    private void onResultRun(int requestCode) {
        if (mZXingView == null) {
            mCreateScanRunable.run();
            mReStartScanRunable.run();
        } else if (PERMISSIONS_REQUEST_CODE_CEATE == requestCode) {
            mCreateScanRunable.run();
        } else if (PERMISSIONS_REQUEST_CODE_RESTART == requestCode) {
            mReStartScanRunable.run();
        }
    }

    //<editor-fold desc="a QRCodeView.Delegate">
    @Override
    public void onScanQRCodeSuccess(String result) {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("text", result));
        Toast.makeText(this, "已复制到剪切板:" + result, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip) || tipText == null) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText != null && tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }
//</editor-fold>

    /**
     * 设置状态栏透明
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = getWindow();
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //导航栏颜色也可以正常设置
            //window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            //int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            //attributes.flags |= flagTranslucentNavigation;
            window.setAttributes(attributes);
        }
    }


    //获取状态栏高度
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
