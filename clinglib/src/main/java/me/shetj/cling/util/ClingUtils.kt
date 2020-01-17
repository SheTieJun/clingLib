package me.shetj.cling.util

import me.shetj.cling.entity.IDevice
import me.shetj.cling.manager.ClingManager
import me.shetj.cling.util.Utils.isNull
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.ServiceType

object ClingUtils {
    /**
     * 通过 ServiceType 获取已选择设备的服务
     *
     * @param serviceType   服务类型
     * @return 服务
     */
    @JvmStatic
    fun findServiceFromSelectedDevice(serviceType: ServiceType?): Service<*, *>? {
        val selectedDevice: IDevice<*>? = ClingManager.instance.selectedDevice
        if (isNull(selectedDevice)) {
            return null
        }
        val device = selectedDevice!!.device as Device<*, *, *>
        return device.findService(serviceType)
    }

    /**
     * 获取 device 的 avt 服务
     *
     * @param device    设备
     * @return 服务
     */
    fun findAVTServiceByDevice(device: Device<*, *, *>): Service<*, *>? {
        return device.findService(ClingManager.AV_TRANSPORT_SERVICE)
    }

    /**
     * 获取控制点
     *
     * @return 控制点
     */
    @JvmStatic
    val controlPoint: ControlPoint?
        get() {
            val controlPoint = ClingManager.instance.controlPoint
            return if (isNull(controlPoint)) {
                null
            } else controlPoint!!.getControlPoint() as ControlPoint?
        }
}