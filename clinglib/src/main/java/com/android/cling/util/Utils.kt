package com.android.cling.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import java.util.*

internal object Utils {


    const val PORT_LISTEN_DEFAULT = 5050
    const val HTTP_SERVLET_KEY = "localeFile"

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

    fun getWiFiIpAddress(context: Context): String {
        var ipAddress = 0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            ipAddress = wifiManager.connectionInfo.ipAddress
        } else {
            val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.run {
                activeNetwork?.let { network ->
                    (getNetworkCapabilities(network)?.transportInfo as? WifiInfo)?.let { wifiInfo ->
                        ipAddress = wifiInfo.ipAddress
                    }
                }
            }
        }
        if (ipAddress == 0) return "127.0.0.1"
        return (ipAddress and 0xFF).toString() + "." + (ipAddress shr 8 and 0xFF) + "." + (ipAddress shr 16 and 0xFF) + "." + (ipAddress shr 24 and 0xFF)
    }

    fun getBaseUrl(context: Context): String {
        return "http://"+getWiFiIpAddress(context)+":"+PORT_LISTEN_DEFAULT+"/$HTTP_SERVLET_KEY/"
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