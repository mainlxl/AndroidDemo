package com.mainli

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import com.mainli.utils.ManifestUtils
import java.util.*

class MainActivity : ListActivity(), View.OnClickListener {
    lateinit var list: ArrayList<ManifestUtils.ActivityItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = ManifestUtils.getActivitiesClass(this, packageName, listOf(MainActivity::class.java, TestActivity::class.java))
        adjustPosition()//调整位置
        if(BuildConfig.DEBUG){
            startActivity(Intent(this,list.first().clazz))
        }
        list.add(0, ManifestUtils.ActivityItem("测试", TestActivity::class.java))
        listAdapter = object : BaseAdapter() {
            override fun getItemId(position: Int): Long {
                return position.toLong();
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val button = Button(this@MainActivity)
                button.setAllCaps(false)
                button.setText("${list[position].name}")
                button.setTag(position)
                button.setOnClickListener(this@MainActivity)
                return button
            }

            override fun getItem(position: Int): Any {
                return list[position]
            }

            override fun getCount(): Int {
                return list.size
            }
        }

    }

    private fun adjustPosition() {
        Collections.reverse(list)
    }

    override fun onClick(v: View) {
        val tag = v.getTag()
        if (tag is Int && tag > -1) {
            startActivity(Intent(this, list[tag].clazz))
        }
    }

}

