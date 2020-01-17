package me.shetj.cling.manager

import me.shetj.cling.service.ClingUpnpService
import org.fourthline.cling.registry.Registry

interface IClingManager<T> : IDLNAManager<T> {
    fun setUpnpService(upnpService: ClingUpnpService?)
    fun setDeviceManager(deviceManager: IDeviceManager<T>?)
    val registry: Registry?
}