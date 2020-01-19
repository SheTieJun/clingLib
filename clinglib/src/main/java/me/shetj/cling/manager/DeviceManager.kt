package me.shetj.cling.manager

import android.content.Context
import me.shetj.cling.control.SubscriptionControl
import me.shetj.cling.entity.ClingDevice
import me.shetj.cling.entity.ClingDeviceList.getClingDeviceList
import me.shetj.cling.entity.IDevice
import me.shetj.cling.util.Utils.isNotNull
import me.shetj.cling.util.Utils.isNull
import org.fourthline.cling.model.meta.Device

class  DeviceManager <T> : IDeviceManager <T> {

    private var mSelectedDevice: ClingDevice? = null
    private var mSubscriptionControl: SubscriptionControl? = null

    init {
        mSubscriptionControl = SubscriptionControl()
    }


    override fun cleanSelectedDevice() {
        if (isNull(mSelectedDevice)) return

        mSelectedDevice!!.isSelected = false
    }

    override fun registerAVTransport(context: Context) {
        if (isNull(mSelectedDevice)) {
            return
        }

        mSubscriptionControl?.registerAVTransport(mSelectedDevice as IDevice<Device<*, *, *>>, context)
    }

    override fun registerRenderingControl(context: Context) {
        if (isNull(mSelectedDevice)) {
            return
        }
        mSubscriptionControl?.registerRenderingControl(mSelectedDevice as IDevice<Device<*, *, *>>, context)
    }

    override fun destroy() {
        if (isNotNull(mSubscriptionControl)) {
            mSubscriptionControl!!.destroy()
        }
    }


    override fun setSelectedDevice(device: IDevice<T>?) {
        mSelectedDevice = device as ClingDevice
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

    override fun getSelectedDevice(): IDevice<T>? {
        return mSelectedDevice as  IDevice<T>
    }

}