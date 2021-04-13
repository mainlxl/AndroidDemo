package com.mainli.activity

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import com.getkeepsafe.relinker.ReLinker
import com.mainli.MyApplication
import com.mainli.R
import com.mainli.blur.BitmapBlur
import com.mainli.utils.BitmapUtils
import com.mainli.utils.SizeUtil
import com.mainli.utils.toDpInt
import com.mainli.view.RoundFrameLayout
import com.seekting.demo_lib.Demo

@Demo(title = "子View统一圆角处理FramLayout", group = ["View"])
class RoundActivity : SeekBarActivity() {
    lateinit var roundFrameLayout: RoundFrameLayout
    override fun attachView(linearlayout: LinearLayout) {
        roundFrameLayout = RoundFrameLayout(this)
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val bitmap = BitmapUtils.getTargetWidthBitmap(resources, R.mipmap.img_test, SizeUtil.getScreenWidthPixels())
        imageView.setImageBitmap(BitmapBlur.blurBitmap(bitmap, 6F) { name -> ReLinker.loadLibrary(MyApplication.getAppContext(), name) })
        roundFrameLayout.addView(imageView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        val layoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        val dp50 = 50.toDpInt()
        layoutParams.leftMargin = dp50
        layoutParams.topMargin = dp50
        layoutParams.rightMargin = dp50
        layoutParams.bottomMargin = dp50
        linearlayout.addView(roundFrameLayout, layoutParams)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        roundFrameLayout.setRadius(progress.toDpInt())
    }

}