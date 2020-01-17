package me.shetj.cling.manager

import android.content.Context
import me.shetj.cling.entity.IControlPoint
import me.shetj.cling.entity.IDevice

interface IDLNAManager <T> {
    /**
     * 搜索所有的设备
     */
    fun searchDevices()

    /**
     * 获取支持 Media 类型的设备
     *
     * @return  设备列表
     */
    val dmrDevices: Collection<*>?

    /**
     * 获取控制点
     *
     * @return  控制点
     */
    val controlPoint: IControlPoint<*>?

    /**
     * 获取选中的设备
     *
     * @return  选中的设备
     */
    /**
     * 设置选中的设备
     * @param device    已选中设备
     */
    var selectedDevice: IDevice<T>?

    /**
     * 取消选中的设备
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