package me.shetj.cling.manager

import android.content.Context
import me.shetj.cling.entity.ClingControlPoint
import me.shetj.cling.entity.ClingDevice
import me.shetj.cling.entity.IControlPoint
import me.shetj.cling.entity.IDevice
import me.shetj.cling.service.ClingUpnpService
import me.shetj.cling.util.ListUtils.isEmpty
import me.shetj.cling.util.Utils.isNull
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.types.DeviceType
import org.fourthline.cling.model.types.ServiceType
import org.fourthline.cling.model.types.UDADeviceType
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.registry.Registry
import java.util.*

class ClingManager private constructor() : IClingManager<Device<*, *, *>?> {
    private var mUpnpService: ClingUpnpService? = null
    private var mDeviceManager: IDeviceManager<Device<*, *, *>?>? = null

    val isInit: Boolean
        get() = !isNull(mUpnpService)

    override fun searchDevices() {
        if (!isNull(mUpnpService)) {
            mUpnpService!!.controlPoint.search()
        }
    }

    override val dmrDevices: Collection<ClingDevice>?
        get() {
            if (isNull(mUpnpService)) {
                return null
            }
            val devices = mUpnpService!!.registry.getDevices(DMR_DEVICE_TYPE)
            if (isEmpty(devices)) {
                return null
            }
            val clingDevices: MutableCollection<ClingDevice> = ArrayList()
            for (device in devices) {
                val clingDevice = ClingDevice(device!!)
                clingDevices.add(clingDevice)
            }
            return clingDevices
        }

    override val controlPoint: IControlPoint<*>?
        get() {
            if (isNull(mUpnpService)) {
                return null
            }
            ClingControlPoint.instance?.setControlPoint(mUpnpService!!.controlPoint)
            return ClingControlPoint.instance
        }


    override val registry: Registry
        get() = mUpnpService!!.registry

    override var selectedDevice: IDevice<Device<*, *, *>?>?
        get() = if (isNull(mDeviceManager)) {
            null
        } else mDeviceManager!!.selectedDevice
        set(device) {
            mDeviceManager!!.selectedDevice = device
        }

    override fun cleanSelectedDevice() {
        if (isNull(mDeviceManager)) {
            return
        }
        mDeviceManager!!.cleanSelectedDevice()
    }

    override fun registerAVTransport(context: Context?) {
        if (isNull(mDeviceManager)) return
        mDeviceManager!!.registerAVTransport(context)
    }

    override fun registerRenderingControl(context: Context?) {
        if (isNull(mDeviceManager)) return
        mDeviceManager!!.registerRenderingControl(context)
    }

    override fun setUpnpService(upnpService: ClingUpnpService?) {
        mUpnpService = upnpService
    }

    override fun setDeviceManager(deviceManager:IDeviceManager<Device<*, *, *>?>?) {
        mDeviceManager = deviceManager
    }


    override fun destroy() {
        mUpnpService!!.onDestroy()
        mDeviceManager!!.destroy()
    }

    companion object {
        //    public static final ServiceType CONTENT_DIRECTORY_SERVICE = new UDAServiceType("ContentDirectory");
        @JvmField
        val AV_TRANSPORT_SERVICE: ServiceType = UDAServiceType("AVTransport")

        /** 控制服务  */
        @JvmField
        val RENDERING_CONTROL_SERVICE: ServiceType = UDAServiceType("RenderingControl")
        val DMR_DEVICE_TYPE: DeviceType = UDADeviceType("MediaRenderer")

        private var INSTANCE: ClingManager? = null

        @JvmStatic
        val instance: ClingManager
            get() {
                if (isNull(INSTANCE)) {
                    INSTANCE = ClingManager()
                }
                return INSTANCE!!
            }
    }



}