package me.shetj.cling.entity

import org.fourthline.cling.model.meta.Device

class ClingDevice(private val mDevice: Device<*, *, *>) : IDevice<Device<*, *, *>?> {

    var isSelected = false

    override val device: Device<*, *, *>
        get() = mDevice

    val name:String
        get() {
            return device.details.friendlyName
        }
}