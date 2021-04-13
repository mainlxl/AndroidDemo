package com.mainli.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mainli.R;
import com.mainli.animutils.AnimationsContainer;
import com.seekting.demo_lib.Demo;


/**
 * TITLE
 * Created by shixiaoming on 16/12/27.
 * 来源: https://github.com/VDshixiaoming/AnimationTest
 *
 * Android3.0(API 11之后)引入了BitmapFactory.Options.inBitmap字段，设置此字段之后解码方法会尝试复用一张存在的Bitmap。
 * 这意味着Bitmap的内存被复用，避免了内存的回收及申请过程，显然性能表现更佳。不过，使用这个字段有几点
 * 声明可被复用的Bitmap必须设置inMutable为true；
 * Android4.4(API 19)之前只有格式为jpg、png，同等宽高（要求苛刻），inSampleSize为1的Bitmap才可以复用；
 * Android4.4(API 19)之前被复用的Bitmap的inPreferredConfig会覆盖待分配内存的Bitmap设置的inPreferredConfig；
 * Android4.4(API 19)之后被复用的Bitmap的内存必须大于需要申请内存的Bitmap的内存；
 * Android4.4(API 19)之前待加载Bitmap的Options.inSampleSize必须明确指定为1。
 * <p>
 * 作者：双十二技术哥
 * 链接：https://www.jianshu.com/p/e49ec7d053b3
 * 來源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
@Demo(title = "帧动画优化内存",group = {"其他"})
public class AnimationsContainerTestActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button playBtn1, playBtn2;
    private AnimationDrawable animationDrawable;
    private int mode;
    AnimationsContainer.FramesSequenceAnimation animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animations_container_test_activity);

        imageView = findViewById(R.id.imgview);
        playBtn1 = findViewById(R.id.play_btn1);
        playBtn2 = findViewById(R.id.play_btn2);
        playBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (animationDrawable != null && animationDrawable.isRunning()) {
                    animationDrawable.stop();
                    playBtn2.setText("普通帧动画 - START");
                }
                if (animation == null) {
                    animation = AnimationsContainer.getInstance(R.array.loading_anim, 58).createProgressDialogAnim(imageView);
                }
                if (!switchBtn1()) {
                    animation.start();
                } else {
                    animation.stop();
                }
                animationDrawable = null;
            }
        });

        playBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (animation != null && animation.isRunning()) {
                    animation.stop();
                    playBtn1.setText("优化帧动画 - START");
                }
                if (animationDrawable == null) {
                    imageView.setImageResource(R.drawable.loading_anim);
                    animationDrawable = (AnimationDrawable) imageView.getDrawable();
                }
                if (!switchBtn2()) {
                    animationDrawable.start();
                } else {
                    animationDrawable.stop();
                }

            }
        });


    }

    //控制开关
    private boolean switchBtn1() {
        boolean returnV = animation.isRunning();
        playBtn1.setText(returnV ? "优化帧动画 - START" : "优化帧动画 - STOP");
        return returnV;
    }

    //控制开关
    private boolean switchBtn2() {
        boolean returnV = animationDrawable.isRunning();
        playBtn2.setText(returnV ? "普通帧动画 - START" : "普通帧动画 - STOP");
        return returnV;
    }
}
