package com.android.cling.listener

import com.android.cling.entity.ClingDevice

internal interface DeviceListChangedListener {
    /**
     * 某设备被发现之后回调该方法
     * @param device    被发现的设备
     */
    fun onDeviceAdded(device: ClingDevice?)

    /**
     * 在已发现设备中 移除了某设备之后回调该接口
     * @param device    被移除的设备
     */
    fun onDeviceRemoved(device: ClingDevice?)
}