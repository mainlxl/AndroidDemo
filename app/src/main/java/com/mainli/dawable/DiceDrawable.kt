package com.mainli.dawable

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.widget.ImageView
import com.mainli.R

/**
 * Created by mobimagic on 2018/2/27.
 */
class DiceDrawable(val context: Context) : AnimationDrawable() {
    private val DURATION = 100
    private val drawables = intArrayOf(R.mipmap.dice_anim_1, R.mipmap.dice_anim_2, R.mipmap.dice_anim_3, R.mipmap.dice_anim_4)
    private val resultDrawables = intArrayOf(R.mipmap.dice_1, R.mipmap.dice_2, R.mipmap.dice_3, R.mipmap.dice_4, R.mipmap.dice_5, R.mipmap.dice_6)
    private val END_INDEX = drawables.size - 1
    private var repeteCount = 0
    var finishCount = 3
    private var resultDrawable: Drawable? = null
    private var resultindex = -1
    private var isSwitchTarget = true
    var diceAnimationListener:DiceAnimationListener?=null
    var oneShotImageView: ImageView? = null
        set(value) {
            value?.setImageDrawable(this)
            field = value
        }

    init {
        for (i in drawables.indices) {
            addFrame(ContextCompat.getDrawable(context, drawables[i]), DURATION)
        }
    }

    override fun start() {
        startSpecifyResult((Math.random() * resultDrawables.size + 1).toInt())
    }

    /**
     * @param resultNumber 1-6 设置最后显示点数
     */
    fun startSpecifyResult(resultNumber: Int) {
        setOneShot(false)
        repeteCount = 0
        resultindex = (resultNumber - 1) % resultDrawables.size
        resultDrawable = ContextCompat.getDrawable(context, resultDrawables[resultindex])
        if (isRunning) {
            unscheduleSelf(null)//关闭运行状态
        }
        super.start()
    }


    override fun selectDrawable(index: Int): Boolean {
        val b = super.selectDrawable(index)
        if (END_INDEX == index) {
            ++repeteCount
            if (repeteCount >= finishCount) {
                setOneShot(true)
                stop()//setFrame中会再次把mRunning设置成true照成状态错误,启动是手动调用设置为false
                oneShotImageView?.setImageDrawable(resultDrawable)
                diceAnimationListener?.onFinishDiceAnimation()
            }
        }
        return b
    }

//    override fun draw(canvas: Canvas?) {
//        if (oneShotImageView == null && repeteCount >= finishCount && resultDrawable != null) {
//            resultDrawable!!.setBounds(bounds)
//            resultDrawable!!.draw(canvas)
//        } else {
//            super.draw(canvas)
//        }
//    }
}
interface DiceAnimationListener{
    fun onFinishDiceAnimation()
}
