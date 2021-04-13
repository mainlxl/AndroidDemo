package com.mainli.utils

fun Int.toDp(): Float {
    return SizeUtil.dp2Px(this.toFloat())
}
fun Int.toDpInt(): Int {
    return SizeUtil.dp2Px(this.toFloat()).toInt()
}