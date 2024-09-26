package com.mainli.activity;

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.mainli.R
import com.mainli.utils.toDpInt
import com.seekting.demo_lib.Demo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Demo(title = "webp动图", group = ["UI"])
class WebpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val size = 50.toDpInt()
        val imageView = WebpImageView(this, null)
        setContentView(
            FrameLayout(this).apply {
                setBackgroundColor(0xffc7edcc.toInt())
                addView(imageView, FrameLayout.LayoutParams(size, size, Gravity.CENTER))
                setOnClickListener {
                    if (imageView.isRunning()) {
                        imageView.stop()
                    } else {
                        imageView.start()
                    }
                }
            }, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )


    }
}

class WebpImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs),
    Animatable {
    val defaultIconRes = R.mipmap.ic_robot
    var startAnimatableDrawable: WebpDrawable? = null
    var loopDrawable: WebpDrawable? = null
    val centerCrop = CenterCrop()
    private var isRunning = false


    init {
        setImageResource(defaultIconRes)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Glide.with(this).asDrawable().load(R.raw.tu_turn).optionalTransform(centerCrop)
            .override(w)
            .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(centerCrop))
            .preload()
        Glide.with(this).asDrawable().load(R.raw.tu_blink).optionalTransform(centerCrop)
            .override(w)
            .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(centerCrop))
            .preload()
    }


    override fun start() {
        isRunning = true
        setImageResource(defaultIconRes)
        GlobalScope.launch(Dispatchers.IO) {
            if (startAnimatableDrawable == null) {
                startAnimatableDrawable = loadWebpDrawable(R.raw.tu_turn)
                startAnimatableDrawable!!.onFrameReady()
            }
            if (loopDrawable == null) {
                loopDrawable = loadWebpDrawable(R.raw.tu_blink)
                loopDrawable!!.onFrameReady()
            }
            withContext(Dispatchers.Main) {
                if (isRunning) {
                    startAnimatableDrawable?.let { startDrawable ->
                        setImageDrawable(startDrawable)
                        startDrawable.loopCount = 1
                        startDrawable.registerAnimationCallback(object :
                            Animatable2Compat.AnimationCallback() {
                            override fun onAnimationEnd(drawable: Drawable?) {
                                if (startAnimatableDrawable != null) {
                                    startAnimatableDrawable = null
                                    setImageDrawable(loopDrawable)
                                    loopDrawable?.startFromFirstFrame()
                                }
                            }
                        })
                        startDrawable.startFromFirstFrame()
                    }
                }
            }

        }
    }

    private fun loadWebpDrawable(@RawRes @DrawableRes resourceId: Int) =
        Glide.with(this@WebpImageView).`as`(WebpDrawable::class.java).load(resourceId)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL).submit(width, height).get()

    override fun stop() {
        isRunning = false
        setImageResource(defaultIconRes)
        startAnimatableDrawable?.let {
            it.stop()
            startAnimatableDrawable = null
        }
        loopDrawable?.let {
            it.stop()
            loopDrawable = null
        }
    }

    override fun isRunning(): Boolean {
        return isRunning
    }
}

