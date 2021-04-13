package com.mainli.activity;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mainli.utils.SizeUtil;
import com.mainli.utils.ViewUtils;
import com.mainli.view.TagLayout;
import com.seekting.demo_lib.Demo;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
@Demo(title = "测试自定义View",group = {"View"})
public class TestViewActivity extends SeekBarActivity {

    private View mChild;
    private int[] colors = new int[]{0xdddd0000, Color.GREEN, Color.YELLOW, Color.CYAN};
    private int[] textSize = new int[]{9, 8, 7, 6};

    @Override
    public void attachView(@NotNull LinearLayout linearlayout) {
//        mChild = new DashboardView(this);
//        mChild = new PieView(this);
//        mChild = new CamearDemoView(this);
        ViewGroup view = new TagLayout(this);
        view.setBackgroundColor(Color.BLACK);
        int dp3 = SizeUtil.dp2PixelsInt(10);
        view.setPadding(dp3, dp3, dp3, dp3);
//        Math.random()
        Log.d("Mainli", "---------------------------------------------------------------------------------");
        for (int i = 0; i < 60; i++) {
            final TextView child = new TextView(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewUtils.clipRoundView(child, SizeUtil.dp2PixelsInt(2));
            }
            String text = new Random().nextInt(150) + "";
            int dp2 = SizeUtil.dp2PixelsInt(2);
            child.setPadding(dp2, dp2, dp2, dp2);
            child.setText(text);
            //TODO 测试竖直对齐方式
//            child.setTextSize(SizeUtil.sp2Px(textSize[new Random().nextInt(4)]));
            child.setBackgroundColor(colors[i % colors.length]);
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
//            params.leftMargin = SizeUtil.dp2PixelsInt(5);
            params.rightMargin = SizeUtil.dp2PixelsInt(5);
            params.bottomMargin = SizeUtil.dp2PixelsInt(5);
//            params.topMargin = SizeUtil.dp2PixelsInt(5);
            view.addView(child, params);
//            Log.d("Mainli---------", text);
        }

        mChild = view;
        linearlayout.addView(mChild, new LinearLayout.LayoutParams(SizeUtil.dp2PixelsInt(200), LinearLayout.LayoutParams.WRAP_CONTENT));
        linearlayout.setBackgroundColor(0xffc7edcc);
        linearlayout.setGravity(Gravity.CENTER);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        ((DashboardView) mChild).setPointerCount(progress);
//        ((PieView) mChild).setCurrentPostion(progress);
//        ((CamearDemoView) mChild).setRotate(progress);
    }

    @Override
    public int max() {
        return 720;
    }

}
