package me.shetj.cling.manager

import android.content.Context
import me.shetj.cling.entity.ClingDevice
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.registry.RegistryListener

interface IDLNAManager {
    /**
     * 搜索所有的设备
     */
    fun searchDevices()

    /**
     * 获取支持 Media 类型的设备
     *
     * @return  设备列表
     */
    val dmrDevices: Collection<ClingDevice>?

    /**
     * 获取控制点
     *
     * @return  控制点
     */
    val controlPoint: ControlPoint?

    /**
     * 获取选中的设备
     *
     * @return  选中的设备
     */
    /**
     * 设置选中的设备
     * @param device    已选中设备
     */
    fun setSelectDevice(selectedDevice: ClingDevice)

    fun getSelectedDevice(): ClingDevice?

    /**
     * 取消选中的设备
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
     * 添加监听
     */
    fun addListener(registryListener: RegistryListener)

    /**
     * 移除监听
     */
    fun removeListener(registryListener: RegistryListener)

    /**
     * 销毁
     */
    fun destroy()

}