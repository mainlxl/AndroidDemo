package com.mainli.activity

import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.mainli.R
import com.mainli.view.NumberCaptchaInputView
import com.seekting.demo_lib.Demo

/**
 * Created by Mainli on 2018-4-8.
 */
@Demo(title = "验证码控件", group = ["View"])
class NumberCaptchaActivity : AppCompatActivity() {
    lateinit var view: NumberCaptchaInputView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_captcha)
        view = findViewById<NumberCaptchaInputView>(R.id.number_captcha)
        view.setListener(object : NumberCaptchaInputView.OnCaptchaListener {
            override fun onCaptchaTextSize(captcha: CharSequence, size: Int) {
                Log.i("Mainli", "验证码输入大小变化 - captcha: $captcha , size: $size");
            }

            override fun onCaptchaInputFinish(captcha: CharSequence?) {
                Log.i("Mainli", "完成输入 - captcha: $captcha");
            }
        })
        var view2 = findViewById<NumberCaptchaInputView>(R.id.number_captcha2)
        view2.setCaptchaDraw(MyCaptchaDraw(resources.displayMetrics.density))//设置扩展样式
    }
}

//扩展样式
private class MyCaptchaDraw(private val density: Float) : NumberCaptchaInputView.CaptchaDraw {
    private val CURRENT_CURSOR_COLOR = 0xFF687FFF.toInt()
    private val DEFAULT_CURSOR_COLOR: Int = 0x42000000
    private val NUMBER_COLOR = 0xDE000000.toInt()
    private var textFixHeight: Float = 0.toFloat()
    private var cornersSize: Float = 0.toFloat()
    private var gapWidth: Float = 0.toFloat()

    private var numberOccupyWidth: Int = 0
    private var left: Int = 0
    private var top: Int = 0
    private var bottom: Float = 0f

    private fun dp2Px(dp: Int): Float {
        return density * dp + 0.5f
    }

    override fun initPaint(numberPaint: Paint, cursorPaint: Paint) {
        numberPaint.textAlign = Paint.Align.CENTER
        numberPaint.color = NUMBER_COLOR
        cursorPaint.style = Paint.Style.STROKE
        val dp2 = dp2Px(2)
        cursorPaint.strokeWidth = dp2
        cornersSize = dp2
    }

    override fun onItemDraw(canvas: Canvas, position: Int, text: CharSequence, numberPaint: Paint, cursorPaint: Paint) {
        val s = text.toString()
        var endIndex = text.length - 1;
        val start = ((numberOccupyWidth + gapWidth) * position + left).toInt()
        if (endIndex == -1 && position == 0) {
            cursorPaint.setColor(CURRENT_CURSOR_COLOR)
            canvas.drawLine(start.toFloat(), bottom, (start + numberOccupyWidth).toFloat(), bottom, cursorPaint)
        } else if (position == endIndex) {
            cursorPaint.setColor(CURRENT_CURSOR_COLOR)
            canvas.drawText(s.get(position).toString(), (start + start + numberOccupyWidth shr 1).toFloat(), bottom * 0.5f - textFixHeight, numberPaint)
            canvas.drawLine(start.toFloat(), bottom, (start + numberOccupyWidth).toFloat(), bottom, cursorPaint)
        } else if (position < endIndex) {
            cursorPaint.setColor(DEFAULT_CURSOR_COLOR)
            canvas.drawText(s.get(position).toString(), (start + start + numberOccupyWidth shr 1).toFloat(), bottom * 0.5f - textFixHeight, numberPaint)
            canvas.drawLine(start.toFloat(), bottom, (start + numberOccupyWidth).toFloat(), bottom, cursorPaint)
        } else {
            cursorPaint.setColor(DEFAULT_CURSOR_COLOR)
            canvas.drawLine(start.toFloat(), bottom, (start + numberOccupyWidth).toFloat(), bottom, cursorPaint)
        }
    }

    override fun onDrawMeasure(numberOccupyWidth: Int, numberOccupyHeight: Int, gapWitch: Float, left: Int, top: Int, right: Int, bottom: Int, textFixHeight: Float) {
        this.numberOccupyWidth = numberOccupyWidth
        this.gapWidth = gapWitch
        this.left = left
        this.top = top
        this.top = top
        this.bottom = bottom.toFloat()
        this.textFixHeight = textFixHeight
    }

}