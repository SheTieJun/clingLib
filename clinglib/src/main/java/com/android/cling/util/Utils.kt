package com.android.cling.util

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.util.*

internal object Utils {
    @JvmStatic
    fun isNull(obj: Any?): Boolean {
        return obj == null
    }

    @JvmStatic
    fun isNotNull(obj: Any?): Boolean {
        return !isNull(obj)
    }

    /**
     * 把时间戳转换成 00:00:00 格式
     * @param timeMs    时间戳
     * @return 00:00:00 时间格式
     */
    @JvmStatic
    fun getStringTime(timeMs: Int): String {
        val formatBuilder = StringBuilder()
        val formatter = Formatter(formatBuilder, Locale.getDefault())
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        formatBuilder.setLength(0)
        return formatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString()
    }

    /**
     * 把 00:00:00 格式转成时间戳
     * @param formatTime    00:00:00 时间格式
     * @return 时间戳(毫秒)
     */
    @JvmStatic
    fun getIntTime(formatTime: String): Int {
        if (isNull(formatTime)) {
            return 0
        }
        val tmp = formatTime.split(":").toTypedArray()
        if (tmp.size < 3) {
            return 0
        }
        val second = Integer.valueOf(tmp[0]) * 3600 + Integer.valueOf(tmp[1]) * 60 + Integer.valueOf(tmp[2])
        return second * 1000
    }

    fun isEmpty(list: Collection<*>?): Boolean {
        return !(list != null && list.isNotEmpty())
    }
}


internal class CircleMessageHandler(
    private val duration: Long,
    private val runnable: Runnable,
) : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
        runnable.run()
        sendEmptyMessageDelayed(MSG, duration)
    }

    fun start(delay: Long = 0L) {
        stop()
        sendEmptyMessageDelayed(MSG, delay)
    }

    fun isStart() = hasMessages(MSG)

    fun stop() {
        removeMessages(MSG)
    }

    companion object {
        private const val MSG = 101
    }
}