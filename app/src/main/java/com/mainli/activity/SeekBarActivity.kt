package com.mainli.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import com.mainli.utils.toDpInt

abstract class SeekBarActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        val linearLayout2 = LinearLayout(this)
        linearLayout2.orientation = LinearLayout.VERTICAL
        attachView(linearLayout2)
        val layoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        linearLayout.addView(linearLayout2, layoutParams)
        val seekBar = addSeekBar(linearLayout)
        setContentView(linearLayout)
        onProgressChanged(seekBar, seekBar.progress, false)
    }

    private fun addSeekBar(linearLayout: LinearLayout): SeekBar {
        val seekBar = SeekBar(this)
        seekBar.max = max()
        seekBar.progress = progress()
        seekBar.setOnSeekBarChangeListener(this)
        val layoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        val dp = 30.toDpInt()
        layoutParams.leftMargin = dp
        layoutParams.topMargin = dp
        layoutParams.rightMargin = dp
        layoutParams.bottomMargin = dp
        linearLayout.addView(seekBar, layoutParams)
        return seekBar
    }

    abstract fun attachView(linearlayout: LinearLayout)

    open fun max(): Int = 100
    open fun progress(): Int = max().shr(1)

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

}