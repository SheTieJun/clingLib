package me.shetj.cling.manager

import android.content.Context
import me.shetj.cling.entity.IDevice

interface IDeviceManager<T> {
    /**
     * 获取选中设备
     */
    /**
     * 设置选中设备
     */
    var selectedDevice: IDevice<T>?

    /**
     * 取消选中设备
     */
    fun cleanSelectedDevice()

    /**
     * 监听投屏端 AVTransport 回调
     * @param context   用于接收到消息发广播
     */
    fun registerAVTransport(context: Context?)

    /**
     * 监听投屏端 RenderingControl 回调
     * @param context   用于接收到消息发广播
     */
    fun registerRenderingControl(context: Context?)

    /**
     * 销毁
     */
    fun destroy()
}