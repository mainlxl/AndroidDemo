package com.mainli.activity;

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.mainli.utils.toDpInt
import com.seekting.demo_lib.Demo
import com.seekting.demo_lib.R

@Demo(title = "测试阴影绘制", group = ["UI"])
class ShadowActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ConstraintLayout(this).apply {
                clipChildren = false
                setBackgroundColor(Color.WHITE)
                addView(
                    View(context).apply {
                        id = R.id.container
                    }, ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0).apply {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToTop = R.id.tabMode
                    }
                )
                addView(
                    TabShadowView(context),
                    ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30.toDpInt())
                        .apply {
                            bottomToTop = R.id.tabMode
                        }
                )
                addView(
                    FrameLayout(context).apply {
                        id = R.id.tabMode
                        addView(
                            TextView(context).apply {
                                text = "1111"
                            }, ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        )
                    },
                    ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50.toDpInt())
                        .apply {
                            topToBottom = R.id.container
                            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        }
                )
            }, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

}

class TabShadowView(context: Context) : View(context) {

    private val paintShadow = Paint(Paint.ANTI_ALIAS_FLAG)
    var path: Path = Path()
    var isTopHalfCircle = true
    val squareSide = 69.toDpInt()

    init {
        paintShadow.setColor(Color.TRANSPARENT)
        paintShadow.setStyle(Paint.Style.STROKE);
        paintShadow.setShadowLayer(24f, 0f, -6f, 0xfa000000.toInt())
        isClickable = false
        paintShadow.strokeWidth = 1f
    }


    override fun onDraw(canvas: Canvas) {
        if (isTopHalfCircle) {
            canvas.drawPath(path, paintShadow)
        } else {
            canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paintShadow)
        }
    }

    // 不能点击紧作为阴影展示
    override fun onTouchEvent(event: MotionEvent?): Boolean = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.setClipBounds(Rect(0, 0, w, h))
        val halfW = w.shr(1)
        val radius = squareSide.shr(1)
        val x1 = (halfW - radius).toFloat()
        val y = h.toFloat()

        path.reset()
        path.moveTo(0f, y)
        path.rLineTo(x1, 0f)
        val top = y - radius * 0.587f
        val rectF = RectF(x1, top, x1 + squareSide, top + squareSide)
        path.addArc(rectF, 180f, 180f)
        path.moveTo(rectF.right, y)
        path.lineTo(w.toFloat(), y)
        path.close()
    }
}