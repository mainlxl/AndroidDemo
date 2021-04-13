package com.mainli.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.mainli.R
import com.mainli.utils.AttachPosition
import com.mainli.view.MultiPointTouch.MultiPointRelayView
import com.seekting.demo_lib.Demo

@Demo(title = "测试LayoutChangeListener,实现LinearLayout右上角加View", group = ["Test"])
class TestLayoutChangeListenerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_layout_change_listener)
        val viewGroup = findViewById<ViewGroup>(R.id.rootView)
        val (textView1, layoutParams1) = create("右下角")
        AttachPosition.attachRightBottom(viewGroup, textView1, layoutParams1);
        val (textView2, layoutParams2) = create("右上角")
        AttachPosition.attachRightTop(viewGroup, textView2, layoutParams2);
        val (textView3, layoutParams3) = create("左上角")
        AttachPosition.attachLeftTop(viewGroup, textView3, layoutParams3);
        val (textView4, layoutParams4) = create("左下角")
        AttachPosition.attachLeftBottom(viewGroup, textView4, layoutParams4);
//        val (textView5, layoutParams5) = create("居中")
        AttachPosition.attachCenter(viewGroup, MultiPointRelayView(this), LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private fun create(msg: String): Pair<TextView, LinearLayout.LayoutParams> {
        val textView = TextView(this)
        textView.setBackgroundColor(Color.BLACK)
        textView.text = msg
        textView.setTextColor(Color.WHITE)
        textView.setPadding(20, 20, 20, 20)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(100, 100, 100, 100)
        return Pair(textView, layoutParams)
    }

}