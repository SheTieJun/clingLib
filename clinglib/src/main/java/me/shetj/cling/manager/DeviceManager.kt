package me.shetj.cling.manager

import me.shetj.cling.control.SubscriptionControl
import me.shetj.cling.entity.ClingDevice
import me.shetj.cling.entity.ClingDeviceList.getClingDeviceList
import me.shetj.cling.util.Utils.isNotNull
import me.shetj.cling.util.Utils.isNull

internal class  DeviceManager  : IDeviceManager  {

    private var mSelectedDevice: ClingDevice? = null
    private var mSubscriptionControl: SubscriptionControl  = SubscriptionControl()


    override fun cleanSelectedDevice() {
        if (isNull(mSelectedDevice)) return

        mSelectedDevice!!.isSelected = false
    }

    override fun registerAVTransport() {
        if (isNull(mSelectedDevice)) {
            return
        }
        mSubscriptionControl.registerAVTransport(mSelectedDevice!!  )
    }

    override fun registerRenderingControl() {
        if (isNull(mSelectedDevice)) {
            return
        }
        mSubscriptionControl.registerRenderingControl(mSelectedDevice!!)
    }

    override fun destroy() {
        if (isNotNull(mSubscriptionControl)) {
            mSubscriptionControl.destroy()
        }
    }


    override fun setSelectedDevice(device: ClingDevice) {
        mSelectedDevice = device
        // 重置选中状态
        val clingDeviceList: Collection<ClingDevice> =
            getClingDeviceList()
        if (isNotNull(clingDeviceList)) {
            clingDeviceList.forEach {
                it.isSelected = false
            }
        }
        // 设置选中状态
        mSelectedDevice?.isSelected = true
    }

    override fun getSelectedDevice(): ClingDevice? {
        return mSelectedDevice
    }

}