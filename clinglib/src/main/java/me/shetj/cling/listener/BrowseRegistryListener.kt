package me.shetj.cling.listener

import android.util.Log
import me.shetj.cling.entity.ClingDevice
import me.shetj.cling.entity.ClingDeviceList
import me.shetj.cling.manager.ClingManager
import me.shetj.cling.util.Utils.isNotNull
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.registry.DefaultRegistryListener
import org.fourthline.cling.registry.Registry

class BrowseRegistryListener : DefaultRegistryListener() {
    private var mOnDeviceListChangedListener: DeviceListChangedListener? = null
    /* Discovery performance optimization for very slow Android devices! */
    override fun remoteDeviceDiscoveryStarted(registry: Registry, device: RemoteDevice) { // 在这里设备拥有服务 也木有 action。。
//        deviceAdded(device);
    }

    override fun remoteDeviceDiscoveryFailed(registry: Registry, device: RemoteDevice, ex: Exception) {
        Log.e(TAG, "remoteDeviceDiscoveryFailed device: " + device.displayString)
        deviceRemoved(device)
    }

    /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */
    override fun remoteDeviceAdded(registry: Registry, device: RemoteDevice) {
        deviceAdded(device)
    }

    override fun remoteDeviceRemoved(registry: Registry, device: RemoteDevice) {
        deviceRemoved(device)
    }

    override fun localDeviceAdded(registry: Registry, device: LocalDevice) { //        deviceAdded(device); // 本地设备 已加入
    }

    override fun localDeviceRemoved(registry: Registry, device: LocalDevice) { //        deviceRemoved(device); // 本地设备 已移除
    }

    private fun deviceAdded(device: Device<*, *, *>) {
        Log.e(TAG, "deviceAdded")
        if (device.type != ClingManager.DMR_DEVICE_TYPE) {
            Log.e(TAG, "deviceAdded called, but not match")
            return
        }
        if (isNotNull(mOnDeviceListChangedListener)) {
            val clingDevice = ClingDevice(device)
            ClingDeviceList.getInstance().addDevice(clingDevice)
            mOnDeviceListChangedListener!!.onDeviceAdded(clingDevice)
        }
    }

    fun deviceRemoved(device: Device<*, *, *>?) {
        Log.e(TAG, "deviceRemoved")
        if (isNotNull(mOnDeviceListChangedListener)) {
            val clingDevice = ClingDeviceList.getInstance().getClingDevice(device)
            if (clingDevice != null) {
                ClingDeviceList.getInstance().removeDevice(clingDevice)
                mOnDeviceListChangedListener!!.onDeviceRemoved(clingDevice)
            }
        }
    }

    fun setOnDeviceListChangedListener(onDeviceListChangedListener: DeviceListChangedListener?) {
        mOnDeviceListChangedListener = onDeviceListChangedListener
    }

    companion object {
        private val TAG = BrowseRegistryListener::class.java.simpleName
    }
}