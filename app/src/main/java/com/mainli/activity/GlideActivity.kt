package com.mainli.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet
import com.mainli.R
import com.mainli.glide.GlideApp
import com.seekting.demo_lib.Demo


/**
 * Created by lixiaoliang on 2018-4-13.
 */
@Demo(title = "Glide加载大图", group = ["框架"])
class GlideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide)
//        GlideApp.with(this).load("http://j20.oss-cn-beijing.aliyuncs.com/1524467663253645082.png").into(findViewById<ImageView>(R.id.imag1))

        val image = findViewById<FinalWidthImageView>(R.id.imag)
        image.setInitSize(500, 200)
        GlideApp.with(this).load("https://camo.githubusercontent.com/b703cdae175f3e664eef49333a7c23bd3a2497e8/68747470733a2f2f692e696d6775722e636f6d2f594f4f714a50532e706e67").into(image)

    }
}

class FinalWidthImageView : AppCompatImageView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var ratio: Float = 0F;
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (ratio > 0F) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (ratio * width).toInt()
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        if (w != widthSize) {
//            val m = Matrix()
//            val scale: Float = w.toFloat() / widthSize.toFloat()
//            m.setScale(scale, scale)
//            imageMatrix = m
//        }
    }

    override fun setScaleType(scaleType: ScaleType?) {
//        super.setScaleType(scaleType)
    }

    private var widthSize = 0
    private var heightSize = 0
    fun setInitSize(widthSize: Int, heightSize: Int) {
        if (widthSize > 0) {
            if (heightSize > 2 * widthSize) {
                ratio = 2F;
                super.setScaleType(ScaleType.MATRIX)
            } else {
                ratio = (heightSize * 1f) / widthSize
                super.setScaleType(ScaleType.CENTER_CROP)
            }
        } else {
            ratio = 1F
            super.setScaleType(ScaleType.CENTER_CROP)
        }
        if (this.widthSize != widthSize || this.heightSize != heightSize) {
            this.widthSize = widthSize
            this.heightSize = heightSize
        }
    }
}