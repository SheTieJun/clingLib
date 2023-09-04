package com.android.cling.entity

import org.fourthline.cling.model.meta.Device

class ClingDevice(private val mDevice: Device<*, *, *>) {

    var isSelected = false

    val device: Device<*, *, *>
        get() = mDevice

    val name:String
        get() {
            return device.details.friendlyName
        }
}