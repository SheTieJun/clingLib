package me.shetj.cling.manager

import android.content.Context
import me.shetj.cling.entity.ClingDevice

interface IDeviceManager {
    /**
     * 获取选中设备
     */
    fun getSelectedDevice() :ClingDevice?
    /**
     * 设置选中设备
     */
    fun setSelectedDevice(device: ClingDevice)

    /**
     * 取消选中设备
     */
    fun cleanSelectedDevice()

    /**
     * 监听投屏端 AVTransport 回调
     * @param context   用于接收到消息发广播
     */
    fun registerAVTransport()

    /**
     * 监听投屏端 RenderingControl 回调
     * @param context   用于接收到消息发广播
     */
    fun registerRenderingControl()

    /**
     * 销毁
     */
    fun destroy()
}