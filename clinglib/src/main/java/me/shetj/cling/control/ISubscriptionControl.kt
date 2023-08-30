package me.shetj.cling.control

import android.content.Context
import me.shetj.cling.entity.ClingDevice

internal interface ISubscriptionControl  {
    /**
     * 监听投屏端 AVTransport 回调
     */
    fun registerAVTransport(device: ClingDevice)

    /**
     * 监听投屏端 RenderingControl 回调
     */
    fun registerRenderingControl(device: ClingDevice)

    /**
     * 销毁: 释放资源
     */
    fun destroy()
}