package com.mainli.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mainli.R
import com.seekting.demo_lib.Demo

@Demo(title = "自定义ViewPager", group = ["UI"])
class TestViewPagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_view_pager)
    }
}