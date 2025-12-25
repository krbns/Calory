package com.kurban.calory.core.format

import kotlin.math.roundToInt

fun Double.roundToOne(): String {
    val rounded = (this * 10.0).roundToInt() / 10.0
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
}
