package com.mainli.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.mainli.dawable.DiceAnimationListener
import com.mainli.dawable.DiceDrawable
import com.seekting.demo_lib.Demo

/**
 * Created by mobimagic on 2018/2/27.
 */
@Demo(title = "骰子DrawableDice", group = ["Drawable"])
class DiceActivity : AppCompatActivity(), View.OnClickListener, DiceAnimationListener {
    private var canSwitch = true
    override fun onFinishDiceAnimation() {
        canSwitch = true
    }

    override fun onClick(v: View?) {
        if (canSwitch) {
            canSwitch = false
            diceDrawable1.oneShotImageView = v as ImageView
            diceDrawable1.start()
        } else {
            Toast.makeText(this@DiceActivity, "等一下", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var diceDrawable1: DiceDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        diceDrawable1 = DiceDrawable(this)
//        imageView.setImageDrawable(diceDrawable1)
        diceDrawable1.diceAnimationListener = this
        diceDrawable1.finishCount = 3

    }

    private fun initView() {
        var ll = LinearLayout(this)
        ll.setOrientation(LinearLayout.VERTICAL)
        for (i in 0..3) {
            val imageView = ImageView(this)
            imageView.setBackgroundColor(0xffc7edcc.toInt())
            val layoutParams = LinearLayout.LayoutParams(200, 200)
            layoutParams.gravity = Gravity.CENTER
            imageView.setOnClickListener(this)
            ll.addView(imageView, layoutParams)
        }
        setContentView(ll, LinearLayout.LayoutParams(-1, -1))
    }

    override fun onDestroy() {
        super.onDestroy()
        diceDrawable1.stop()
    }
}