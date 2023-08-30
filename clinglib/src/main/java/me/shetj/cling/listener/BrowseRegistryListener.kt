package me.shetj.cling.listener

import android.util.Log
import me.shetj.cling.ClingManager
import me.shetj.cling.entity.ClingDevice
import me.shetj.cling.entity.ClingDeviceList
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.registry.DefaultRegistryListener
import org.fourthline.cling.registry.Registry

internal class BrowseRegistryListener : DefaultRegistryListener() {
    private var mOnDeviceListChangedListener: DeviceListChangedListener? = null

    /* Discovery performance optimization for very slow Android devices! */
    override fun remoteDeviceDiscoveryStarted(registry: Registry, device: RemoteDevice) {
        Log.i(TAG, "remoteDeviceDiscoveryStarted: " + device.displayString)
        deviceAdded(device);
    }

    override fun remoteDeviceDiscoveryFailed(registry: Registry, device: RemoteDevice, ex: Exception) {
        Log.e(TAG, "remoteDeviceDiscoveryFailed device: " + device.displayString)
        deviceRemoved(device)
    }

    /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */
    override fun remoteDeviceAdded(registry: Registry, device: RemoteDevice) {
        Log.i(TAG, "remoteDeviceAdded: " + device.displayString)
        deviceAdded(device)
    }

    override fun remoteDeviceRemoved(registry: Registry, device: RemoteDevice) {
        Log.i(TAG, "remoteDeviceRemoved: " + device.displayString)
        deviceRemoved(device)
    }

    override fun localDeviceAdded(registry: Registry, device: LocalDevice) {
        Log.i(TAG, "localDeviceAdded: " + device.displayString)
        deviceAdded(device); // 本地设备 已加入
    }

    override fun localDeviceRemoved(registry: Registry, device: LocalDevice) {
        Log.i(TAG, "localDeviceRemoved: " + device.displayString)
        deviceRemoved(device); // 本地设备 已移除
    }

    private fun deviceAdded(device: Device<*, *, *>) {
        if (device.type != ClingManager.DMR_DEVICE_TYPE) {
            return
        }
        val clingDevice = ClingDevice(device)
        ClingDeviceList.addDevice(clingDevice)
        ClingManager.getInstant().updateCurrentDevices(ClingDeviceList.getClingDeviceList())
        mOnDeviceListChangedListener?.onDeviceAdded(clingDevice)
    }

    private fun deviceRemoved(device: Device<*, *, *>) {
        val clingDevice = ClingDeviceList.getClingDevice(device)
        if (clingDevice != null) {
            ClingDeviceList.removeDevice(clingDevice)
            ClingManager.getInstant().updateCurrentDevices(ClingDeviceList.getClingDeviceList())
            mOnDeviceListChangedListener?.onDeviceRemoved(clingDevice)
        }
    }

    fun setOnDeviceListChangedListener(onDeviceListChangedListener: DeviceListChangedListener?) {
        mOnDeviceListChangedListener = onDeviceListChangedListener
    }

    companion object {
        private val TAG = BrowseRegistryListener::class.java.simpleName
    }
}