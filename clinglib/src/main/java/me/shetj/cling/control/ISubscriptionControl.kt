package me.shetj.cling.control

import android.content.Context
import me.shetj.cling.entity.IDevice

interface ISubscriptionControl<T> {
    /**
     * 监听投屏端 AVTransport 回调
     */
    fun registerAVTransport(device: IDevice<T>, context: Context)

    /**
     * 监听投屏端 RenderingControl 回调
     */
    fun registerRenderingControl(device: IDevice<T>, context: Context)

    /**
     * 销毁: 释放资源
     */
    fun destroy()
}