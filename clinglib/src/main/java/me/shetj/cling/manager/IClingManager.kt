package me.shetj.cling.manager

import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.registry.Registry

interface IClingManager  : IDLNAManager  {
    fun setUpnpService(upnpService: AndroidUpnpService?)
    fun setDeviceManager(deviceManager: IDeviceManager?)
    val registry: Registry?
}